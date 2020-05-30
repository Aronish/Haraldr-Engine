package engine.graphics;

import engine.main.OrthographicCamera;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.math.Vector4f;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

@SuppressWarnings({"unused", "WeakerAccess"})
@Deprecated
public class Renderer2D
{
    public static SceneData2D sceneData = new SceneData2D();

    public static void beginScene(@NotNull OrthographicCamera camera)
    {
        sceneData.setViewMatrix(camera.getViewMatrix());
        Shader.DEFAULT2D.bind(); // Versions of draw* without shader parameter will use default shader.
    }

    public static void drawQuad(Vector3f position)
    {
        drawQuad(position, new Vector4f(1.0f));
    }

    public static void drawQuad(Vector3f position, Vector4f color)
    {
        Texture.DEFAULT_WHITE.bind(0);
        Shader.DEFAULT2D.setMatrix4f("model", Matrix4f.createTranslate(position));
        Shader.DEFAULT2D.setMatrix4f("view", sceneData.getViewMatrix());
        Shader.DEFAULT2D.setMatrix4f("projection", Matrix4f.orthographic);
        Shader.DEFAULT2D.setVector4f("color", color);

        SceneData2D.QUAD.bind();
        SceneData2D.QUAD.drawElements();
    }

    public static void drawQuad(Vector3f position, @NotNull Shader shader)
    {
        drawQuad(position, shader, new Vector4f(1.0f));
    }

    public static void drawQuad(Vector3f position, @NotNull Shader shader, Vector4f color)
    {
        shader.bind();
        Texture.DEFAULT_WHITE.bind(0);
        shader.setMatrix4f("model", Matrix4f.createTranslate(position));
        shader.setMatrix4f("view", sceneData.getViewMatrix());
        shader.setMatrix4f("projection", Matrix4f.orthographic);
        shader.setVector4f("color", color);

        SceneData2D.QUAD.bind();
        SceneData2D.QUAD.drawElements();
    }

    public static void drawQuad(Vector3f position, @NotNull Texture texture)
    {
        drawQuad(position, texture, new Vector4f(1.0f));
    }

    public static void drawQuad(Vector3f position, @NotNull Texture texture, Vector4f tintColor)
    {
        texture.bind(0);
        Shader.DEFAULT2D.setMatrix4f("model", Matrix4f.createTranslate(position));
        Shader.DEFAULT2D.setMatrix4f("view", sceneData.getViewMatrix());
        Shader.DEFAULT2D.setMatrix4f("projection", Matrix4f.orthographic);
        Shader.DEFAULT2D.setVector4f("color", tintColor);

        SceneData2D.QUAD.bind();
        SceneData2D.QUAD.drawElements();
    }

    public static void drawQuad(Vector3f position, @NotNull Shader shader, @NotNull Texture texture)
    {
        drawQuad(position, shader, texture, new Vector4f(1.0f));
    }

    public static void drawQuad(Vector3f position, @NotNull Shader shader, @NotNull Texture texture, Vector4f tintColor)
    {
        shader.bind();
        texture.bind(0);
        shader.setMatrix4f("model", Matrix4f.createTranslate(position));
        shader.setMatrix4f("view", sceneData.getViewMatrix());
        shader.setMatrix4f("projection", Matrix4f.orthographic);
        shader.setVector4f("color", tintColor);

        SceneData2D.QUAD.bind();
        SceneData2D.QUAD.drawElements();
    }
}