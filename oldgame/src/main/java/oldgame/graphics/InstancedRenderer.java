package oldgame.graphics;

import haraldr.graphics.ShaderDataType;
import haraldr.graphics.VertexBuffer;
import haraldr.graphics.VertexBufferElement;
import haraldr.graphics.VertexBufferLayout;
import haraldr.graphics.Wrapper;
import haraldr.main.ArrayUtils;
import haraldr.scene.OrthographicCamera;
import haraldr.math.Matrix4f;
import oldgame.gameobject.GameObject;
import oldgame.world.Grid;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InstancedRenderer implements RenderSystem
{
    private ArrayList<Float> matrices = new ArrayList<>();
    private VertexBuffer instancedMatrixBuffer;

    public InstancedRenderer()
    {
        VertexBufferLayout layout = new VertexBufferLayout
        (
                new VertexBufferElement(ShaderDataType.MAT4),
                new VertexBufferElement(ShaderDataType.MAT4),
                new VertexBufferElement(ShaderDataType.MAT4),
                new VertexBufferElement(ShaderDataType.MAT4)
        );
        instancedMatrixBuffer = new VertexBuffer(2500000, layout, VertexBuffer.Usage.DYNAMIC_DRAW);
        /////SETUP ATTRIBUTES//////////////////////////
        for (GameObject gameObject : GameObject.instancedObjects)
        {
            gameObject.getModel().getVertexArray().bind();
            instancedMatrixBuffer.bind();//Must be bound after model due to the vertex buffer being bound with the array.
            int nextAttribIndex = gameObject.getModel().getVertexArray().getNextAttribIndex();
            for (VertexBufferElement element : instancedMatrixBuffer.getLayout())
            {
                Wrapper.enableVertexAttribArrayWrapper(nextAttribIndex);
                Wrapper.vertexAttribPointer(nextAttribIndex, element, instancedMatrixBuffer.getLayout().getStride());
                Wrapper.vertexAttribDivisor(nextAttribIndex, 1);
                nextAttribIndex += 1;
            }
            gameObject.getModel().getVertexArray().setNextAttribIndex(nextAttribIndex);
        }
    }

    @Override
    public void renderGridCells(@NotNull OrthographicCamera camera, List<Grid.GridCell> gridCells)
    {
        Shaders.INSTANCED_SHADER.bind();
        Shaders.INSTANCED_SHADER.setMatrix4f("view", camera.getViewMatrix());
        Shaders.INSTANCED_SHADER.setMatrix4f("projection", Matrix4f.identity()); //Wrong
        instancedMatrixBuffer.bind();
        for (GameObject gameObject : GameObject.instancedObjects)
        {
            matrices.clear();
            for (Grid.GridCell gridCell : gridCells)
            {
                matrices.addAll(gridCell.getMatrices(gameObject));
            }
            if (matrices.size() != 0)
            {
                instancedMatrixBuffer.setSubDataUnsafe(ArrayUtils.toPrimitiveArrayF(matrices));
                gameObject.getModel().getVertexArray().bind();
                gameObject.getModel().getVertexArray().drawElementsInstanced(matrices.size() / 16);
            }
        }
    }
}