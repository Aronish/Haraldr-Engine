package oldgame.graphics;

import engine.graphics.Renderer2D;
import engine.graphics.Shader;
import engine.main.OrthographicCamera;
import engine.math.Matrix4f;
import oldgame.gameobject.Entity;
import oldgame.main.EntryPoint;
import org.jetbrains.annotations.NotNull;

import static engine.main.Application.MAIN_LOGGER;

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

    public static void render(@NotNull OrthographicCamera camera, @NotNull Shader shader, @NotNull Entity entity)
    {
        shader.bind();
        shader.setMatrix4f("matrix", entity.getMatrix());
        shader.setMatrix4f("view", camera.getViewMatrix());
        shader.setMatrix4f("projection", Matrix4f.orthographic);
        entity.getGameObjectType().getModel().getVertexArray().bind();
        entity.getGameObjectType().getModel().getVertexArray().drawElements();
    }
}
