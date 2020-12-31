package haraldr.graphics;

import haraldr.scene.Camera;
import haraldr.main.Window;
import haraldr.scene.Scene3D;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class Renderer3D //TODO: Clean up this mess
{
    public static final VertexArray SCREEN_QUAD = new VertexArray();

    static
    {
        float[] quadVertexData = {
                -1f,  1f,   0f, 1f,
                 1f,  1f,   1f, 1f,
                 1f, -1f,   1f, 0f,
                -1f, -1f,   0f, 0f
        };
        VertexBuffer quadVertices = new VertexBuffer(
                quadVertexData,
                new VertexBufferLayout(new VertexBufferElement(ShaderDataType.FLOAT2), new VertexBufferElement(ShaderDataType.FLOAT2)),
                VertexBuffer.Usage.STATIC_DRAW
        );
        SCREEN_QUAD.setVertexBuffers(quadVertices);
        SCREEN_QUAD.setIndexBufferData(new int[] { 0, 3, 2, 0, 2, 1 });
    }

    private static final UniformBuffer matrixBuffer = new UniformBuffer(140);
    private static Shader postProcessingShader = Shader.create("internal_shaders/hdr_gamma_correct.glsl");
    private static float exposure = 0.5f;

    public static void init(Window.WindowProperties initialWindowProperties)
    {
        //currentFramebuffer = new Framebuffer();
        //if (initialWindowProperties.samples > 0)
        //{
        //    currentFramebuffer.setColorAttachment(new Framebuffer.MultisampledColorAttachment(initialWindowProperties.width, initialWindowProperties.height, Framebuffer.ColorAttachment.Format.RGB16F, initialWindowProperties.samples));
        //    currentFramebuffer.setDepthBuffer(new Framebuffer.MultisampledRenderBuffer(initialWindowProperties.width, initialWindowProperties.height, Framebuffer.RenderBuffer.Format.DEPTH_24_STENCIL_8, initialWindowProperties.samples));
        //}
        //else if (initialWindowProperties.samples == 0)
        //{
        //    currentFramebuffer.setColorAttachment(new Framebuffer.ColorAttachment(initialWindowProperties.width, initialWindowProperties.height, Framebuffer.ColorAttachment.Format.RGB16F));
        //    currentFramebuffer.setDepthBuffer(new Framebuffer.RenderBuffer(initialWindowProperties.width, initialWindowProperties.height, Framebuffer.RenderBuffer.Format.DEPTH_24_STENCIL_8));
        //}
        //else throw new IllegalArgumentException("Multisample sample count cannot be below 0");
    }

    public static void addExposure(float pExposure)
    {
        if (exposure < 0.0001f) exposure = 0.0001f;
        exposure += pExposure * exposure; // Makes it seem more linear towards the lower exposure levels.
    }

    public static void dispose()
    {
        SCREEN_QUAD.delete();
        matrixBuffer.delete();
    }

    /////RENDERING/////////////////////////////////////////////////

    public static void begin(@NotNull Window window, Camera camera, Framebuffer framebuffer) //TODO: Unnecessary in current form?
    {
        matrixBuffer.bind(0);
        matrixBuffer.setSubDataUnsafe(camera.getViewMatrix().matrix, 0);
        matrixBuffer.setSubDataUnsafe(camera.getProjectionMatrix().matrix, 64);
        matrixBuffer.setSubDataUnsafe(camera.getRawPosition(), 128);
        framebuffer.bind();
        Renderer.enableDepthTest();
        Renderer.clear(Renderer.ClearMask.COLOR_DEPTH_STENCIL);
    }

    public static void renderSceneToTexture(Window window, Camera camera, Scene3D scene, RenderTexture renderTexture)
    {
        begin(window, camera, renderTexture.getFramebuffer());
        Renderer.setViewPort(0, 0, (int)renderTexture.getSize().getX(), (int)renderTexture.getSize().getY());
        scene.onRender();
        end(window, renderTexture.getFramebuffer());
        Renderer.setViewPort(0, 0, window.getWidth(), window.getHeight());
    }

    public static void end(@NotNull Window window, Framebuffer framebuffer)
    {
        framebuffer.unbind();
        Renderer.clear(Renderer.ClearMask.COLOR_DEPTH_STENCIL);
        ///// POST PROCESSING //////
        //postProcessingShader.bind();
        //postProcessingShader.setFloat("u_Exposure", exposure);
        //SCREEN_QUAD.bind();
        //SCREEN_QUAD.drawElements();
    }
}
