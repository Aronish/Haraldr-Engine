package engine.graphics.pbr;

import engine.graphics.Framebuffer;
import engine.graphics.Renderer3D;
import engine.graphics.Shader;
import engine.graphics.UniformBuffer;
import engine.graphics.lighting.SceneLights;
import engine.main.Window;
import engine.math.Matrix4f;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

@SuppressWarnings("unused")
public class PBRRenderer extends Renderer3D
{
    private static final UniformBuffer matrixBuffer = new UniformBuffer(128);
    private static SceneLights sceneLights = new SceneLights();

    private static Framebuffer postProcessingFrameBuffer;
    private static Shader shader = new Shader("default_shaders/hdr_gamma_correct.glsl");
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

    public static void init(@NotNull Window window)
    {
        postProcessingFrameBuffer = new Framebuffer(window);
    }

    public static void begin()
    {
        matrixBuffer.bind(0);
        matrixBuffer.setDataUnsafe(perspectiveCamera.getViewMatrix().matrix, 0);
        matrixBuffer.setDataUnsafe(Matrix4f.perspective.matrix, 64);
        sceneLights.bind();
        postProcessingFrameBuffer.bind();
        clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static void end()
    {
        postProcessingFrameBuffer.unbind();
        clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        postProcess();
    }

    private static void postProcess()
    {
        shader.bind();
        shader.setFloat(exposure, "exposure");
        postProcessingFrameBuffer.getColorAttachment().bind();
        Renderer3D.SCREEN_QUAD.bind();
        Renderer3D.SCREEN_QUAD.drawElements();
    }

    public static void dispose()
    {
        sceneLights.dispose();
        matrixBuffer.delete();
    }
}
