package engine.graphics;

import engine.main.PerspectiveCamera;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Renderer3D
{
    public static SceneData3D sceneData = new SceneData3D();

    public static Light light = new Light(new Vector3f(1f, 2f, 3f), new Vector3f(1f));

    public static void beginScene(@NotNull PerspectiveCamera camera)
    {
        sceneData.setViewMatrix(camera.getViewMatrix());
        sceneData.setViewPosition(camera.getPosition());
        Shader.DEFAULT3D.bind();
    }

    public static void drawCube(Vector3f position)
    {
        drawCube(position, 1f);
    }

    public static void drawCube(Vector3f position, float scale)
    {
        Shader.DEFAULT3D.setMatrix4f(Matrix4f.translate(position, false).multiply(Matrix4f.scale(new Vector3f(scale))), "model");
        Shader.DEFAULT3D.setMatrix4f(sceneData.getViewMatrix(), "view");
        Shader.DEFAULT3D.setMatrix4f(Matrix4f.perspective, "projection");
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
        shader.setMatrix4f(sceneData.getViewMatrix(), "view");
        shader.setMatrix4f(Matrix4f.perspective, "projection");
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
        shader.bind();
        shader.setMatrix4f(Matrix4f.translate(position, false), "model");
        shader.setMatrix4f(sceneData.getViewMatrix(), "view");
        shader.setMatrix4f(Matrix4f.perspective, "projection");
        //Lighting
        shader.setVector3f(mesh.getMaterial().getDiffuse(), "diffuseColor");
        shader.setFloat(mesh.getMaterial().getSpecularExponent(), "specularExponent");
        shader.setFloat(mesh.getMaterial().getOpacity(), "opacity");

        shader.setVector3f(light.getColor(), "lightColor");
        shader.setVector3f(light.getPosition(), "lightPosition");
        shader.setVector3f(sceneData.getViewPosition(), "viewPosition");

        mesh.getVertexArray().bind();
        mesh.getVertexArray().drawElements();
    }
}