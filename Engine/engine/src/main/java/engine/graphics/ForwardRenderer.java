package engine.graphics;

import engine.graphics.lighting.SceneLights;
import engine.graphics.material.DiffuseMaterial;
import engine.graphics.material.Material;
import engine.main.ArrayUtils;
import engine.main.PerspectiveCamera;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ForwardRenderer extends Renderer3D
{
    private final UniformBuffer matrixBuffer = new UniformBuffer(128);
    private SceneLights sceneLights = new SceneLights();

    private Map<Material, Batcher> batchedMaterials = new HashMap<>();

    @Override
    public void begin(@NotNull PerspectiveCamera camera)
    {
        viewPosition = camera.getPosition();
        matrixBuffer.bind(0);
        matrixBuffer.setDataUnsafe(camera.getViewMatrix().matrix, 0);
        matrixBuffer.setDataUnsafe(Matrix4f.perspective.matrix, 64);
        sceneLights.bind();
        //System.out.println("BEGIN");
    }

    @Override
    public void end()
    {
        for (Batcher batcher : batchedMaterials.values())
        {
            batcher.end();
        }
    }

    private final VertexArray vertexArray = ObjParser.loadMesh("models/cube.obj");
    private final Mesh CUBE = new Mesh(vertexArray.getVertexBuffers().get(0).getData(), vertexArray.getIndices());
    private Material material = new DiffuseMaterial(new Vector3f(0.8f, 0.2f, 0.3f));

    @Override
    public void drawCube(Vector3f position)
    {
        if (!batchedMaterials.containsKey(material))
        {
            batchedMaterials.put(material, new Batcher(material));
        }
        batchedMaterials.get(material).submitMesh(CUBE, Matrix4f.translate(position));
    }

    public void setSceneLights(SceneLights sceneLights)
    {
        this.sceneLights = sceneLights;
    }

    public SceneLights getSceneLights()
    {
        return sceneLights;
    }

    public void dispose()
    {
        sceneLights.dispose();
        matrixBuffer.delete();
    }

    private class Batcher
    {
        private static final int MAX_VERTICES_PER_BATCH = 360000;
        private final Material batchMaterial;

        private VertexArray batch = new VertexArray(MAX_VERTICES_PER_BATCH);
        private VertexBuffer vertexBuffer;
        private List<Float> vertexData = new ArrayList<>();

        private Batcher(Material material)
        {
            batchMaterial = material;
            vertexBuffer = new VertexBuffer(MAX_VERTICES_PER_BATCH * ObjParser.VERTEX_ELEMENT_COUNT * 4, new VertexBufferLayout(
                    new VertexBufferElement(ShaderDataType.FLOAT3), //Position
                    new VertexBufferElement(ShaderDataType.FLOAT3), //Normal
                    new VertexBufferElement(ShaderDataType.FLOAT2), //UV
                    new VertexBufferElement(ShaderDataType.FLOAT3)  //Tangent
            ), true);
            batch.setVertexBuffers(vertexBuffer);
            int[] indices = new int[MAX_VERTICES_PER_BATCH];
            for (int i = 0; i < MAX_VERTICES_PER_BATCH; i++)
            {
                indices[i] = i;
            }
            batch.setIndicesUnsafe(indices);
        }

        private void submitMesh(@NotNull Mesh mesh, Matrix4f transformationMatrix)
        {
            if (vertexData.size() + mesh.getVertexData().size() > MAX_VERTICES_PER_BATCH * ObjParser.VERTEX_ELEMENT_COUNT) end();
            this.vertexData.addAll(transform(mesh.getVertexData(), transformationMatrix));
        }

        private void end()
        {
            vertexBuffer.bind();
            vertexBuffer.setDataUnsafe(ArrayUtils.toPrimitiveArrayF(vertexData));
            flush();
            vertexData.clear();
        }

        private void flush()
        {
            batchMaterial.bind();
            batchMaterial.getShader().setVector3f(viewPosition, "viewPosition");
            batch.bind();
            batch.drawElements();
        }

    }

    private static @NotNull List<Float> transform(@NotNull List<Float> vertexData, Matrix4f transformationMatrix)
    {
        List<Float> tranformed = new ArrayList<>(vertexData);
        for (int index = 0; index < vertexData.size(); index += ObjParser.VERTEX_ELEMENT_COUNT)
        {
            Vector3f transformedVector = getPositionVector(vertexData, index).multiply(transformationMatrix);
            tranformed.set(index, transformedVector.getX());
            tranformed.set(index + 1, transformedVector.getY());
            tranformed.set(index + 2, transformedVector.getZ());
        }
        return tranformed;
    }

    private static @NotNull Vector3f getPositionVector(@NotNull List<Float> vertexData, int n)
    {
        return new Vector3f(vertexData.get(n), vertexData.get(n + 1), vertexData.get(n + 2));
    }
}