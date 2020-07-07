package engine.graphics;

import engine.graphics.lighting.SceneLights;
import engine.main.Camera;
import engine.main.PerspectiveCamera;
import engine.main.Window;
import engine.math.Matrix4f;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
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

    protected static Camera camera = new PerspectiveCamera();
    public static Camera getCamera()
    {
        return camera;
    }

    private static final UniformBuffer matrixBuffer = new UniformBuffer(140);
    private static SceneLights sceneLights = new SceneLights();
    private static Shader postProcessingShader = Shader.create("internal_shaders/hdr_gamma_correct.glsl");
    private static float exposure = 0.5f;

    public static void addExposure(float pExposure)
    {
        if (exposure < 0.0001f) exposure = 0.0001f;
        exposure += pExposure * exposure; // Makes it seem more linear towards the lower exposure levels.
    }

    public static void setSceneLights(SceneLights pSceneLights)
    {
        sceneLights = pSceneLights;
    }

    public static SceneLights getSceneLights()
    {
        return sceneLights;
    }

    public static void dispose()
    {
        SCREEN_QUAD.delete();
        sceneLights.dispose();
        matrixBuffer.delete();
    }

    /////RENDERING////////////////////

    public static void begin(@NotNull Window window)
    {
        matrixBuffer.bind(0);
        matrixBuffer.setDataUnsafe(camera.getViewMatrix().matrix, 0);
        matrixBuffer.setDataUnsafe(Matrix4f.perspective.matrix, 64);
        matrixBuffer.setDataUnsafe(camera.getRawPosition(), 128);
        sceneLights.bind();
        window.getFramebuffer().bind();
        Renderer.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static void end(@NotNull Window window)
    {
        glBindTexture(GL_TEXTURE_2D, window.getFramebuffer().getColorAttachmentTexture());
        window.getFramebuffer().unbind();
        Renderer.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        ///// POST PROCESSING //////
        postProcessingShader.bind();
        postProcessingShader.setFloat("u_Exposure", exposure);
        SCREEN_QUAD.bind();
        SCREEN_QUAD.drawElements();
    }
}
