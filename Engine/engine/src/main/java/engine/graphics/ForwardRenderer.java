package engine.graphics;

import engine.graphics.material.Material;
import engine.math.Matrix4f;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class ForwardRenderer extends Renderer3D
{
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