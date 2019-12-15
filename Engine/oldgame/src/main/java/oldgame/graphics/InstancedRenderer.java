package oldgame.graphics;

import oldgame.gameobject.GameObject;
import engine.graphics.Shader;
import engine.graphics.ShaderDataType;
import engine.graphics.VertexBuffer;
import engine.graphics.VertexBufferElement;
import engine.graphics.VertexBufferLayout;
import engine.main.ArrayUtils;
import engine.main.Camera;
import engine.math.Matrix4f;
import oldgame.world.Grid;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

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
        instancedMatrixBuffer = new VertexBuffer(2500000, layout, true);
        /////SETUP ATTRIBUTES//////////////////////////
        for (GameObject gameObject : GameObject.instancedObjects)
        {
            gameObject.getModel().getVertexArray().bind();
            instancedMatrixBuffer.bind();//Must be bound after model due to the vertex buffer being bound with the array.
            int nextAttribIndex = gameObject.getModel().getVertexArray().getNextAttribIndex();
            for (VertexBufferElement element : instancedMatrixBuffer.getLayout())
            {
                glEnableVertexAttribArray(nextAttribIndex);
                glVertexAttribPointer(nextAttribIndex, element.getSize(), element.getType(), element.isNormalized(), instancedMatrixBuffer.getLayout().getStride(), element.getOffset());
                glVertexAttribDivisor(nextAttribIndex, 1);
                nextAttribIndex += 1;
            }
            gameObject.getModel().getVertexArray().setNextAttribIndex(nextAttribIndex);
        }
    }

    @Override
    public void renderGridCells(@NotNull Camera camera, List<Grid.GridCell> gridCells)
    {
        Shader.INSTANCED_SHADER.use();
        Shader.INSTANCED_SHADER.setMatrix(camera.getViewMatrix().matrix, "view");
        Shader.INSTANCED_SHADER.setMatrix(Matrix4f.orthographic.matrix, "projection");
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
                glBufferSubData(GL_ARRAY_BUFFER, 0, ArrayUtils.toPrimitiveArrayF(matrices));
                gameObject.getModel().getVertexArray().bind();
                gameObject.getModel().getVertexArray().drawInstanced(matrices.size() / 16);
            }
        }
    }
}