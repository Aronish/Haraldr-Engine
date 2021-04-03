package haraldr.graphics;

import haraldr.scene.Camera;
import haraldr.main.Window;
import haraldr.scene.Scene3D;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class Renderer3D
{
    private static final UniformBuffer matrixBuffer = new UniformBuffer(140);

    public static void dispose()
    {
        matrixBuffer.delete();
    }

    /////RENDERING/////////////////////////////////////////////////

    private static void begin(@NotNull Window window, Camera camera, Framebuffer framebuffer)
    {
        matrixBuffer.bind(0);
        matrixBuffer.setSubDataUnsafe(camera.getViewMatrix().matrix, 0);
        matrixBuffer.setSubDataUnsafe(camera.getProjectionMatrix().matrix, 64);
        matrixBuffer.setSubDataUnsafe(camera.getRawPosition(), 128);
        framebuffer.bind();
        Renderer.enableDepthTest();
        Renderer.clear(Renderer.ClearMask.COLOR_DEPTH_STENCIL);
    }

    public static void renderSceneToTexture(Window window, Camera camera, DynamicScene scene, RenderTexture renderTexture)
    {
        begin(window, camera, renderTexture.getFramebuffer());
        Renderer.setViewPort(0, 0, (int)renderTexture.getSize().getX(), (int)renderTexture.getSize().getY());
        scene.render();
        end(window, renderTexture.getFramebuffer());
        Renderer.setViewPort(0, 0, window.getWidth(), window.getHeight());
    }

    private static void end(@NotNull Window window, Framebuffer framebuffer)
    {
        framebuffer.unbind();
        Renderer.clear(Renderer.ClearMask.COLOR_DEPTH_STENCIL);
    }
}
