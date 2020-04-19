package oldgame.graphics;

import engine.graphics.ShaderDataType;
import engine.graphics.VertexArray;
import engine.graphics.VertexBuffer;
import engine.graphics.VertexBufferElement;
import engine.graphics.VertexBufferLayout;
import engine.graphics.Wrapper;
import engine.main.ArrayUtils;
import engine.main.OrthographicCamera;
import engine.math.Matrix4f;
import oldgame.gameobject.GameObject;
import oldgame.world.Grid;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MultiDrawIndirectRenderer implements RenderSystem
{
    private VertexArray vao;
    private VertexBuffer indirectBuffer;

    private List<Float> matrices = new ArrayList<>();
    private VertexBuffer instancedMatrixBuffer;

    private HashMap<GameObject, Integer> instanceCounts = new HashMap<>(), matrixOffsets = new HashMap<>();
    private List<Integer> indirectData = new ArrayList<>();

    public MultiDrawIndirectRenderer()
    {
        List<Integer> indirectIndices = new ArrayList<>();
        for (int i = 0; i < 6; ++i)
        {
            indirectIndices.add(4 * i);
            indirectIndices.add(4 * i + 1);
            indirectIndices.add(4 * i + 2);
            indirectIndices.add(4 * i);
            indirectIndices.add(4 * i + 2);
            indirectIndices.add(4 * i + 3);
        }
        vao = new VertexArray();
        vao.setIndexBuffer(ArrayUtils.toPrimitiveArrayI(indirectIndices));
        /////MODEL DATA STORE///////////////////////////////////
        List<Float> modelVertexData = new ArrayList<>();
        for (GameObject gameObject : GameObject.instancedObjects)
        {
            float[] vertexData = gameObject.getModel().getVertexArray().getVertexBuffers().get(0).getData();
            modelVertexData.addAll(ArrayUtils.toList(vertexData));
        }

        VertexBufferLayout modelLayout = new VertexBufferLayout
                (
                        new VertexBufferElement(ShaderDataType.FLOAT2),
                        new VertexBufferElement(ShaderDataType.FLOAT2)
                );
        VertexBuffer modelVertexBuffer = new VertexBuffer(ArrayUtils.toPrimitiveArrayF(modelVertexData), modelLayout, false);
        vao.setVertexBuffers(modelVertexBuffer);

        /////MATRICES/////////////////////////////////////
        VertexBufferLayout layout = new VertexBufferLayout
                (
                        new VertexBufferElement(ShaderDataType.MAT4),
                        new VertexBufferElement(ShaderDataType.MAT4),
                        new VertexBufferElement(ShaderDataType.MAT4),
                        new VertexBufferElement(ShaderDataType.MAT4)
                );
        instancedMatrixBuffer = new VertexBuffer(2500000, layout, true);

        vao.bind();
        instancedMatrixBuffer.bind();
        int nextAttribIndex = vao.getNextAttribIndex();
        for (VertexBufferElement element : instancedMatrixBuffer.getLayout())
        {
            Wrapper.enableVertexAttribArrayWrapper(nextAttribIndex);
            Wrapper.vertexAttribPointer(nextAttribIndex, element, instancedMatrixBuffer.getLayout().getStride());
            Wrapper.vertexAttribDivisor(nextAttribIndex, 1);
            nextAttribIndex += 1;
        }
        vao.setNextAttribIndex(nextAttribIndex);
        vao.unbind();

        indirectBuffer = new VertexBuffer(Wrapper.GL_DRAW_INDIRECT_BUFFER, 1000, true);
    }

    @Override
    public void renderGridCells(@NotNull OrthographicCamera camera, List<Grid.GridCell> gridCells)
    {
        Shaders.MULTI_DRAW_SHADER.bind();
        Shaders.MULTI_DRAW_SHADER.setMatrix4f(camera.getViewMatrix(), "view");
        Shaders.MULTI_DRAW_SHADER.setMatrix4f(Matrix4f.orthographic, "projection");
        /////COLLECT MATRICES/////////////////////////////////////////////////////////////////
        matrices.clear();
        instanceCounts.replaceAll((key, value) -> 0);
        matrixOffsets.replaceAll((key, value) -> 0);
        indirectData.clear();
        int matrixStride = 0;
        int objectCount = 0;
        for (GameObject gameObject : GameObject.instancedObjects)
        {
            for (Grid.GridCell gridCell : gridCells)
            {
                matrices.addAll(gridCell.getMatrices(gameObject));
                int prevCount = instanceCounts.getOrDefault(gameObject, 0);
                instanceCounts.put(gameObject, prevCount + gridCell.getTileCount(gameObject));
            }
            matrixOffsets.put(gameObject, matrixStride);
            matrixStride += instanceCounts.getOrDefault(gameObject, 0);

            List<Integer> entry = new ArrayList<>();
            entry.add(6);                                                      //Indices per instance.
            entry.add(instanceCounts.getOrDefault(gameObject, 0));  //Amount of instances of current object type.
            entry.add(0);                                                      //First index offset, should be 0.
            entry.add(4 * objectCount);                                        //Offset into model data.
            entry.add(matrixOffsets.get(gameObject));                          //Offset into instanced matrix attribute data.
            indirectData.addAll(entry);
            ++objectCount;
        }

        instancedMatrixBuffer.bind();
        instancedMatrixBuffer.setData(ArrayUtils.toPrimitiveArrayF(matrices));

        indirectBuffer.bind();
        indirectBuffer.setData(ArrayUtils.toPrimitiveArrayI(indirectData));

        vao.bind();
        vao.multiDrawIndirect(indirectData.size() / 5);
    }
}