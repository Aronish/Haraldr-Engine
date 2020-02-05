package engine.graphics;

import engine.main.PerspectiveCamera;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glUniform1i;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Renderer3D
{
    public static SceneData3D sceneData = new SceneData3D();
    private static UniformBuffer matrixBuffer = new UniformBuffer(128);

    public static Light light = new Light(new Vector3f(1f, 2f, 3f), new Vector3f(1f, 1f, 0.85f));

    public static void beginScene(@NotNull PerspectiveCamera camera)
    {
        sceneData.setViewPosition(camera.getPosition());
        matrixBuffer.setData(camera.getViewMatrix().matrix, 0);
        matrixBuffer.setData(Matrix4f.perspective.matrix, 64);
        Shader.DEFAULT3D.bind();
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

    public static void drawCube(@NotNull Shader shader, Vector3f position, float scale, Vector3f color)
    {
        shader.bind();
        shader.setMatrix4f(Matrix4f.translate(position, false).multiply(Matrix4f.scale(new Vector3f(scale))), "model");
        //Lighting
        shader.setVector3f(color, "diffuseColor");
        shader.setFloat(16, "specularExponent");
        shader.setFloat(1f, "opacity");

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
        drawMesh(shader, mesh, position, 1f);
    }

    public static void drawMesh(@NotNull Shader shader, @NotNull Mesh mesh, Vector3f position, float scale)
    {
        drawMesh(shader, mesh, SceneData2D.defaultTexture, position, scale); //TODO: Fix SceneData things.
    }

    public static void drawMesh(@NotNull Shader shader, @NotNull Mesh mesh, @NotNull Texture texture, Vector3f position, float scale)
    {
        Material material = mesh.getMaterial();
        shader.bind();
        texture.bind();
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
        glActiveTexture(GL_TEXTURE0);
        texture.bind();
        glActiveTexture(GL_TEXTURE1);
        normalMap.bind();
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
    }
}