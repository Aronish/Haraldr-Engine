package oldgame.graphics;

import engine.graphics.Renderer;
import engine.graphics.Shader;
import engine.main.Camera;
import engine.math.Matrix4f;
import oldgame.gameobject.Entity;
import oldgame.main.EntryPoint;
import org.jetbrains.annotations.NotNull;

import static engine.main.Application.MAIN_LOGGER;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GameRenderer extends Renderer
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

    public static void render(@NotNull Camera camera, @NotNull Shader shader, @NotNull Entity entity)
    {
        shader.use();
        shader.setMatrix(entity.getMatrixArray(), "matrix");
        shader.setMatrix(camera.getViewMatrix().matrix, "view");
        shader.setMatrix(Matrix4f.orthographic.matrix, "projection");
        entity.getGameObjectType().getModel().getVertexArray().bind();
        entity.getGameObjectType().getModel().getVertexArray().draw();

        glBindVertexArray(0); //Without this, the last thing rendered before text is rendered will capture some buffer bindings in TextRenderer.
    }
}
