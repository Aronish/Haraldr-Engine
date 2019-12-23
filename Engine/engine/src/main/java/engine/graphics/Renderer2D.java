package engine.graphics;

import engine.main.OrthograhpicCamera;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.math.Vector4f;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

public class Renderer2D
{
    private static SceneData sceneData = new SceneData();

    public static void clear()
    {
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public static void setClearColor(float r, float g, float b, float a)
    {
        glClearColor(r, g, b, a);
    }

    public static void beginScene(@NotNull OrthograhpicCamera camera)
    {
        sceneData.setViewMatrix(camera.getViewMatrix());
    }

    public static void drawQuad(Vector3f position, @NotNull Shader shader)
    {
        drawQuad(position, shader, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
    }

    public static void drawQuad(Vector3f position, @NotNull Shader shader, Vector4f color)
    {
        shader.bind();
        shader.setMatrix4f(Matrix4f.translate(position, false), "model");
        shader.setMatrix4f(sceneData.getViewMatrix(), "view");
        shader.setMatrix4f(Matrix4f.orthographic, "projection");
        shader.setVector4f(color, "color");

        SceneData.QUAD.bind();
        SceneData.QUAD.draw();
    }

    public static void drawQuad(Vector3f position, Shader shader, Texture texture)
    {
        shader.bind();
        texture.bind();
        shader.setMatrix4f(Matrix4f.translate(position, false), "model");
        shader.setMatrix4f(sceneData.getViewMatrix(), "view");
        shader.setMatrix4f(Matrix4f.orthographic, "projection");

        SceneData.QUAD.bind();
        SceneData.QUAD.draw();
    }
}