package haraldr.graphics;

import haraldr.scene.Camera;
import haraldr.main.Window;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

@SuppressWarnings("unused")
public abstract class Renderer3D
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

    /////RENDERING////////////////////

    public static void begin(@NotNull Window window, Camera camera)
    {
        matrixBuffer.bind(0);
        matrixBuffer.setDataUnsafe(camera.getViewMatrix().matrix, 0);
        matrixBuffer.setDataUnsafe(camera.getProjectionMatrix().matrix, 64);
        matrixBuffer.setDataUnsafe(camera.getRawPosition(), 128);
        window.getFramebuffer().bind();
        Renderer.clear(Renderer.ClearMask.COLOR_DEPTH_STENCIL);
    }

    //TODO: Abstract away render passes
    public static void end(@NotNull Window window)
    {
        glBindTexture(GL_TEXTURE_2D, window.getFramebuffer().getColorAttachmentTexture());
        window.getFramebuffer().unbind();
        Renderer.clear(Renderer.ClearMask.COLOR_DEPTH);
        ///// POST PROCESSING //////
        postProcessingShader.bind();
        postProcessingShader.setFloat("u_Exposure", exposure);
        SCREEN_QUAD.bind();
        SCREEN_QUAD.drawElements();
    }
}
