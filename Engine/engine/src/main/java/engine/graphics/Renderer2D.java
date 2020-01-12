package engine.graphics;

import engine.main.OrthographicCamera;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.math.Vector4f;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

//TODO: More deferred approach. Many unnecessary shader bindings. Batch things together.
@SuppressWarnings({"unused", "WeakerAccess"})
public class Renderer2D
{
    public static SceneData sceneData = new SceneData();

    public static void clear()
    {
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public static void setClearColor(float r, float g, float b, float a)
    {
        glClearColor(r, g, b, a);
    }

    public static void beginScene(@NotNull OrthographicCamera camera)
    {
        sceneData.setViewMatrix(camera.getViewMatrix());
        Shader.DEFAULT.bind(); // Versions of draw* without shader parameter will use default shader.
    }

    public static void drawQuad(Vector3f position)
    {
        drawQuad(position, new Vector4f(1.0f));
    }

    public static void drawQuad(Vector3f position, Vector4f color)
    {
        SceneData.defaultTexture.bind();
        Shader.DEFAULT.setMatrix4f(Matrix4f.translate(position, false), "model");
        Shader.DEFAULT.setMatrix4f(sceneData.getViewMatrix(), "view");
        Shader.DEFAULT.setMatrix4f(Matrix4f.orthographic, "projection");
        Shader.DEFAULT.setVector4f(color, "color");

        SceneData.QUAD.bind();
        SceneData.QUAD.draw();
    }

    public static void drawQuad(Vector3f position, @NotNull Shader shader)
    {
        drawQuad(position, shader, new Vector4f(1.0f));
    }

    public static void drawQuad(Vector3f position, @NotNull Shader shader, Vector4f color)
    {
        shader.bind();
        SceneData.defaultTexture.bind();
        shader.setMatrix4f(Matrix4f.translate(position, false), "model");
        shader.setMatrix4f(sceneData.getViewMatrix(), "view");
        shader.setMatrix4f(Matrix4f.orthographic, "projection");
        shader.setVector4f(color, "color");

        SceneData.QUAD.bind();
        SceneData.QUAD.draw();
    }

    public static void drawQuad(Vector3f position, @NotNull Texture texture)
    {
        drawQuad(position, texture, new Vector4f(1.0f));
    }

    public static void drawQuad(Vector3f position, @NotNull Texture texture, Vector4f tintColor)
    {
        SceneData.defaultTexture.bind();
        texture.bind();
        Shader.DEFAULT.setMatrix4f(Matrix4f.translate(position, false), "model");
        Shader.DEFAULT.setMatrix4f(sceneData.getViewMatrix(), "view");
        Shader.DEFAULT.setMatrix4f(Matrix4f.orthographic, "projection");
        Shader.DEFAULT.setVector4f(tintColor, "color");

        SceneData.QUAD.bind();
        SceneData.QUAD.draw();
    }

    public static void drawQuad(Vector3f position, @NotNull Shader shader, @NotNull Texture texture)
    {
        drawQuad(position, shader, texture, new Vector4f(1.0f));
    }

    public static void drawQuad(Vector3f position, @NotNull Shader shader, @NotNull Texture texture, Vector4f tintColor)
    {
        shader.bind();
        texture.bind();
        shader.setMatrix4f(Matrix4f.translate(position, false), "model");
        shader.setMatrix4f(sceneData.getViewMatrix(), "view");
        shader.setMatrix4f(Matrix4f.orthographic, "projection");
        shader.setVector4f(tintColor, "color");

        SceneData.QUAD.bind();
        SceneData.QUAD.draw();
    }
}