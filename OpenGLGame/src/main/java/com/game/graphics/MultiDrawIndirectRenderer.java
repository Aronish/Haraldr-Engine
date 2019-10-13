package com.game.graphics;

import com.game.ArrayUtils;
import com.game.gameobject.GameObject;

import java.util.ArrayList;
import java.util.List;

import static com.game.Application.MAIN_LOGGER;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL45.glCreateBuffers;

public class MultiDrawIndirectRenderer
{
    public VertexArray vao = new VertexArray();

    public List<Float> matrices = new ArrayList<>();
    public VertexBuffer instancedMatrixBuffer;

    public List<Float> modelVertexData = new ArrayList<>();
    public VertexBuffer modelVertexBuffer;

    public static int indirectBuffer = glCreateBuffers();

    public MultiDrawIndirectRenderer()
    {
        /////MODEL DATA STORE///////////////////////////////////
        for (GameObject gameObject : GameObject.values())
        {
            if (gameObject.instanced)
            {
                float[] vertexData = gameObject.getModel().getVertexArray().getVertexBuffer().getData();
                modelVertexData.addAll(ArrayUtils.toList(vertexData));
            }
        }//Stored correctly

        VertexBufferLayout modelLayout = new VertexBufferLayout
                (
                        new VertexBufferElement(ShaderDataType.FLOAT2, false),
                        new VertexBufferElement(ShaderDataType.FLOAT2, false)
                );
        modelVertexBuffer = new VertexBuffer(ArrayUtils.toPrimitiveArrayF(modelVertexData), modelLayout);
        vao.setVertexBuffer(modelVertexBuffer);

        /////MATRICES/////////////////////////////////////
        VertexBufferLayout layout = new VertexBufferLayout
                (
                        new VertexBufferElement(ShaderDataType.MAT4, false),
                        new VertexBufferElement(ShaderDataType.MAT4, false),
                        new VertexBufferElement(ShaderDataType.MAT4, false),
                        new VertexBufferElement(ShaderDataType.MAT4, false)
                );
        instancedMatrixBuffer = new VertexBuffer(5000000, layout);

        vao.bind();
        instancedMatrixBuffer.bind();
        int nextAttribIndex = vao.getNextAttribIndex();
        for (VertexBufferElement element : instancedMatrixBuffer.getLayout())
        {
            glEnableVertexAttribArray(nextAttribIndex);
            glVertexAttribPointer(nextAttribIndex, element.getSize(), element.getType(), element.isNormalized(), instancedMatrixBuffer.getLayout().getStride(), element.getOffset());
            glVertexAttribDivisor(nextAttribIndex, 1);
            nextAttribIndex += 1;
        }
        vao.setNextAttribIndex(nextAttribIndex);
        vao.unbind();

        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, indirectBuffer);
        glBufferData(GL_DRAW_INDIRECT_BUFFER, 1000, GL_DYNAMIC_DRAW); //EXTRA ERROR IF <100
        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, 0);
    }
}
