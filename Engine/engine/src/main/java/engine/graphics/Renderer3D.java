package engine.graphics;

import engine.graphics.lighting.SceneLights;
import engine.main.PerspectiveCamera;
import engine.main.Window;
import engine.math.Matrix4f;
import engine.math.Vector4f;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

@SuppressWarnings("unused")
public abstract class Renderer3D
{
    public static final VertexArray SCREEN_QUAD = new VertexArray();

    static
    {
        float[] quadVertexData =
                {
                        -1f,  1f,   0f, 1f,
                         1f,  1f,   1f, 1f,
                         1f, -1f,   1f, 0f,
                        -1f, -1f,   0f, 0f
                };
        VertexBuffer quadVertices = new VertexBuffer(
                quadVertexData,
                new VertexBufferLayout(new VertexBufferElement(ShaderDataType.FLOAT2), new VertexBufferElement(ShaderDataType.FLOAT2)),
                false
        );
        SCREEN_QUAD.setVertexBuffers(quadVertices);
        SCREEN_QUAD.setIndexBuffer(new int[] { 0, 3, 2, 0, 2, 1 });
    }

    protected static PerspectiveCamera perspectiveCamera = new PerspectiveCamera(); //TODO: Make camera base class.
    public static PerspectiveCamera getPerspectiveCamera()
    {
        return perspectiveCamera;
    }

    private static final UniformBuffer matrixBuffer = new UniformBuffer(128);
    private static SceneLights sceneLights = new SceneLights();
    private static Framebuffer postProcessingFrameBuffer;
    private static Shader postProcessingShader = Shader.create("default_shaders/hdr_gamma_correct.glsl");
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

    public static void dispose()
    {
        SCREEN_QUAD.delete();
        postProcessingFrameBuffer.delete();
        sceneLights.dispose();
        matrixBuffer.delete();
    }

    /////RENDERING////////////////////

    public static void clear(int mask)
    {
        glClear(mask);
    }

    public static void setClearColor(@NotNull Vector4f color)
    {
        glClearColor(color.getX(), color.getY(), color.getZ(), color.getW());
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
        postProcessingShader.bind();
        postProcessingShader.setFloat(exposure, "exposure");
        postProcessingFrameBuffer.getColorAttachment().bind();
        SCREEN_QUAD.bind();
        SCREEN_QUAD.drawElements();
    }

    /////DEFAULT//////////
/*
    public void drawCube()
    {
        drawCube(Matrix4f.identity());
    }

    public void drawCube(Vector3f position)
    {
        drawCube(Matrix4f.translate(position));
    }

    public void drawCube(Vector3f position, float scale)
    {
        drawCube(Matrix4f.translate(position).multiply(Matrix4f.scale(new Vector3f(scale))));
    }

    public void drawCube(Vector3f position, Vector3f rotationAxis, float rotation)
    {
        drawCube(Matrix4f.translate(position).multiply(Matrix4f.rotate(rotationAxis, rotation)));
    }

    public void drawCube(Vector3f position, float scale, Vector3f rotationAxis, float rotation)
    {
        drawCube(Matrix4f.translate(position).multiply(Matrix4f.rotate(rotationAxis, rotation)).multiply(Matrix4f.scale(new Vector3f(scale))));
    }

    public void drawCube(Matrix4f transformation)
    {
        DiffuseMaterial.DEFAULT.bind();
        Shader.DIFFUSE.setMatrix4f(transformation, "model");
        Shader.DIFFUSE.setVector3f(viewPosition, "viewPosition");
        DefaultModels.CUBE.bind();
        DefaultModels.CUBE.drawElements();
    }

    /////CUSTOM////////////////////////////////////////////////////////////

    public void drawCube(Vector3f position, Material customMaterial)
    {
        drawCube(Matrix4f.translate(position), customMaterial);
    }

    public void drawCube(Vector3f position, float scale, Material customMaterial)
    {
        drawCube(Matrix4f.translate(position).multiply(Matrix4f.scale(new Vector3f(scale))), customMaterial);
    }

    public void drawCube(Vector3f position, Vector3f rotationAxis, float rotation, Material customMaterial)
    {
        drawCube(Matrix4f.translate(position).multiply(Matrix4f.rotate(rotationAxis, rotation)), customMaterial);
    }

    public void drawCube(Vector3f position, float scale, Vector3f rotationAxis, float rotation, Material customMaterial)
    {
        drawCube(Matrix4f.translate(position).multiply(Matrix4f.rotate(rotationAxis, rotation)).multiply(Matrix4f.scale(new Vector3f(scale))), customMaterial);
    }

    public void drawCube(Matrix4f transformation, @NotNull Material customMaterial)
    {
        customMaterial.bind();
        customMaterial.getShader().setMatrix4f(transformation, "model");
        customMaterial.getShader().setVector3f(viewPosition, "viewPosition");
        DefaultModels.CUBE.bind();
        DefaultModels.CUBE.drawElements();
    }
*/
}
