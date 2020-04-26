package engine.graphics;

import engine.graphics.lighting.SceneLights;
import engine.graphics.material.Material;
import engine.main.Window;
import engine.math.Matrix4f;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

@SuppressWarnings("unused")
public class ForwardRenderer extends Renderer3D
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

    private static Map<Material, List<ModelData>> materialBatches = new HashMap<>();

    public static void beginBatch()
    {
        materialBatches.clear();
    }

    /**
     * At the moment, material sorting doesn't seem to improve a whole lot. About the same performance.
     * @param model the model to render.
     */
    public static void submitModel(@NotNull Model model)
    {
        Material key = model.getMaterial();
        if (!materialBatches.containsKey(key))
        {
            materialBatches.put(key, new ArrayList<>());
        }
        materialBatches.get(key).add(new ModelData(model.getMesh(), model.getTransformationMatrix()));
    }

    public static void endBatch()
    {
        for (Material material : materialBatches.keySet())
        {
            material.bind();
            material.getShader().setVector3f(perspectiveCamera.getPosition(), "viewPosition");
            for (ModelData modelData : materialBatches.get(material))
            {
                material.getShader().setMatrix4f(modelData.transformationMatrix, "model");
                modelData.mesh.bind();
                modelData.mesh.drawElements();
            }
        }
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

    private static class ModelData
    {
        private VertexArray mesh;
        private Matrix4f transformationMatrix;

        private ModelData(VertexArray mesh, Matrix4f transformationMatrix)
        {
            this.mesh = mesh;
            this.transformationMatrix = transformationMatrix;
        }
    }

    private static class InstancedData
    {
        private Model model;
        private int count;

        public InstancedData(Model model, int count)
        {
            this.model = model;
            this.count = count;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InstancedData that = (InstancedData) o;
            return model.equals(that.model);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(model);
        }
    }
}