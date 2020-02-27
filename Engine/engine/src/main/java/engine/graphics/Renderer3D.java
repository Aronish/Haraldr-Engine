package engine.graphics;

import engine.main.Application;
import engine.main.PerspectiveCamera;
import engine.math.Matrix4f;
import engine.math.Quaternion;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11.GL_LINES;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Renderer3D
{
    public static SceneData3D sceneData = new SceneData3D();
    private static UniformBuffer matrixBuffer = new UniformBuffer(128);

    public static Light light = new Light(new Vector3f(1f, 2f, 3f), new Vector3f(1f, 1f, 0.85f));
    public static Shader vectorShader = new Shader("default_shaders/lines.vert", "default_shaders/simpleColor.frag");

    public static final Material DEFAULT_MATERIAL = new Material(
            new Vector3f(0.3f),
            new Vector3f(0.8f, 0.2f, 0.3f),
            new Vector3f(0.8f, 0.2f, 0.3f),
            32f,
            1f
    );

    public static void beginScene(@NotNull PerspectiveCamera camera)
    {
        //rotationAxis = camera.getDirection();
        sceneData.setViewPosition(camera.getPosition());
        matrixBuffer.setData(camera.getViewMatrix().matrix, 0);
        matrixBuffer.setData(Matrix4f.perspective.matrix, 64);
        Shader.DEFAULT3D.bind();
    }

    public static void drawVector(Vector3f vector, Vector3f translation)
    {
        vectorShader.bind();
        vectorShader.setMatrix4f(Matrix4f.translate(translation, false), "model");
        vectorShader.setVector3f(new Vector3f(0f, 1f, 0f), "color");
        sceneData.setVector(vector);
        SceneData3D.VECTOR.bind();
        SceneData3D.VECTOR.drawElements(GL_LINES);
    }

    public static void drawCube(Vector3f position)
    {
        drawCube(position, 1f);
    }

    public static void drawCube(Vector3f position, float scale)
    {
        Shader.DEFAULT3D.setMatrix4f(Matrix4f.translate(position, false).multiply(Matrix4f.scale(new Vector3f(scale))), "model");
        SceneData3D.CUBE.bind();
        SceneData3D.CUBE.drawElements();
    }

    public static void drawCube(@NotNull Shader shader, Vector3f position)
    {
        drawCube(shader, position, 1f, new Vector3f(1f));
    }

    public static void drawCube(@NotNull Shader shader, Vector3f position, float scale)
    {
        drawCube(shader, position, scale, new Vector3f(1f));
    }

    public static void drawCube(@NotNull Shader shader, Vector3f position, Vector3f color)
    {
        drawCube(shader, position, 1f, color);
    }

    private static float rotation = 0f;
    private static float sin, cos;
    public static Vector3f rotationAxis = new Vector3f(0f, 1f, 0f);

    public static void drawCube(@NotNull Shader shader, Vector3f position, float scale, Vector3f color)
    {
        rotation += 0.5f;
        sin = (float) Math.sin(Application.time);
        cos = (float) Math.cos(Application.time);
        rotationAxis.set(sin, cos, sin);
        Texture.DEFAULT_TEXTURE.bind(0);
        shader.bind();
        shader.setMatrix4f(Matrix4f.translate(position, false).multiply(Matrix4f.rotate(rotationAxis, rotation)).multiply(Matrix4f.scale(new Vector3f(scale))), "model");
        //Material Properties
        shader.setVector3f(DEFAULT_MATERIAL.getAmbient(), "material.ambientColor");
        shader.setVector3f(color, "material.diffuseColor");
        shader.setVector3f(DEFAULT_MATERIAL.getSpecular(), "material.specularColor");
        shader.setFloat(DEFAULT_MATERIAL.getSpecularExponent(), "material.specularExponent");
        shader.setFloat(DEFAULT_MATERIAL.getOpacity(), "material.opacity");
        //Light
        shader.setVector3f(light.getColor(), "lightColor");
        shader.setVector3f(light.getPosition(), "lightPosition");
        shader.setVector3f(sceneData.getViewPosition(), "viewPosition");

        SceneData3D.CUBE.bind();
        SceneData3D.CUBE.drawArrays();
    }

    public static void drawMesh(@NotNull Shader shader, @NotNull Mesh mesh)
    {
        drawMesh(shader, mesh, new Vector3f());
    }

    public static void drawMesh(@NotNull Shader shader, @NotNull Mesh mesh, Vector3f position)
    {
        drawMesh(shader, mesh, Texture.DEFAULT_TEXTURE, position, 1f);
    }

    public static void drawMesh(@NotNull Shader shader, @NotNull Mesh mesh, Vector3f position, float scale)
    {
        drawMesh(shader, mesh, Texture.DEFAULT_TEXTURE, position, scale); //TODO: Fix SceneData things.
    }

    public static void drawMesh(@NotNull Shader shader, @NotNull Mesh mesh, @NotNull Texture texture, Vector3f position, float scale)
    {
        Material material = mesh.getMaterial();
        shader.bind();
        shader.setInteger(0, "diffuseTexture");
        texture.bind(0);
        shader.setMatrix4f(Matrix4f.translate(position, false).multiply(Matrix4f.scale(new Vector3f(scale))), "model");
        //Material Properties
        shader.setVector3f(material.getAmbient(), "material.ambientColor");
        shader.setVector3f(material.getDiffuse(), "material.diffuseColor");
        shader.setVector3f(material.getSpecular(), "material.specularColor");
        shader.setFloat(material.getSpecularExponent(), "material.specularExponent");
        shader.setFloat(material.getOpacity(), "material.opacity");
        //Light
        shader.setVector3f(light.getColor(), "lightColor");
        shader.setVector3f(light.getPosition(), "lightPosition");
        shader.setVector3f(sceneData.getViewPosition(), "viewPosition");

        mesh.getVertexArray().bind();
        mesh.getVertexArray().drawElements();
    }

    public static void drawMesh(@NotNull Shader shader, @NotNull Mesh mesh, @NotNull Texture texture, @NotNull Texture normalMap, Vector3f position, float scale)
    {
        Material material = mesh.getMaterial();
        shader.bind();
        shader.setInteger(0, "diffuseTexture");
        shader.setInteger(1, "normalMap");
        texture.bind(0);
        normalMap.bind(1);
        shader.setMatrix4f(Matrix4f.translate(position, false).multiply(Matrix4f.rotateZ(90f)).multiply(Matrix4f.scale(new Vector3f(scale))), "model");
        //Material Properties
        shader.setVector3f(material.getAmbient(), "material.ambientColor");
        shader.setVector3f(material.getDiffuse(), "material.diffuseColor");
        shader.setVector3f(material.getSpecular(), "material.specularColor");
        shader.setFloat(material.getSpecularExponent(), "material.specularExponent");
        shader.setFloat(material.getOpacity(), "material.opacity");
        //Light
        shader.setVector3f(light.getColor(), "lightColor");
        shader.setVector3f(light.getPosition(), "lightPosition");
        shader.setVector3f(sceneData.getViewPosition(), "viewPosition");

        mesh.getVertexArray().bind();
        mesh.getVertexArray().drawElements();

        normalMap.unbind(1);
    }
}