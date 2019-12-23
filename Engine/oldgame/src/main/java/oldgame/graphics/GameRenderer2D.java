package oldgame.graphics;

import engine.graphics.Renderer2D;
import engine.graphics.Shader;
import engine.main.OrthograhpicCamera;
import engine.math.Matrix4f;
import oldgame.gameobject.Entity;
import oldgame.main.EntryPoint;
import org.jetbrains.annotations.NotNull;

import static engine.main.Application.MAIN_LOGGER;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GameRenderer2D extends Renderer2D
{
    public static RenderSystem renderSystem;

    static
    {
        switch (EntryPoint.gameRenderSystemType)
        {
            case INSTANCING:
                renderSystem = new InstancedRenderer();
                MAIN_LOGGER.info("Render System: Instancing");
                break;
            case MULTI_DRAW:
                renderSystem = new MultiDrawIndirectRenderer();
                MAIN_LOGGER.info("Render System: MultiDrawIndirect");
                break;
            default:
                MAIN_LOGGER.error("Renderer not initialized with render system!");
                renderSystem = null;
                break;
        }
    }

    public static void render(@NotNull OrthograhpicCamera camera, @NotNull Shader shader, @NotNull Entity entity)
    {
        shader.bind();
        shader.setMatrix4f(entity.getMatrix(), "matrix");
        shader.setMatrix4f(camera.getViewMatrix(), "view");
        shader.setMatrix4f(Matrix4f.orthographic, "projection");
        entity.getGameObjectType().getModel().getVertexArray().bind();
        entity.getGameObjectType().getModel().getVertexArray().draw();

        glBindVertexArray(0); //Without this, the last thing rendered before text is rendered will capture some buffer bindings in TextRenderer.
    }
}
