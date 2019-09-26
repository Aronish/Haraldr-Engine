package com.game.graphics;

import static com.game.Application.MAIN_LOGGER;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL45.glCreateBuffers;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;

public class VertexArrayImpr implements IVertexArray
{
    private static final int[] defaultIndices = {
            0, 1, 2,
            0, 2, 3
    };

    private int vertexArrayID;
    private int indexBufferID;
    private int attribPointerIndex = 0;
    private VertexBuffer vertexBuffer;

    public VertexArrayImpr()
    {
        vertexArrayID = glCreateVertexArrays();
        indexBufferID = glCreateBuffers();
        glBindVertexArray(vertexArrayID);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, defaultIndices, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void setVertexBuffer(VertexBuffer vertexBuffer)
    {
        this.vertexBuffer = vertexBuffer;
        glBindVertexArray(vertexArrayID);
        vertexBuffer.bind();
        for (VertexBufferElement element : vertexBuffer.getLayout())
        {
            glEnableVertexAttribArray(attribPointerIndex);
            glVertexAttribPointer(attribPointerIndex, element.getSize(), element.getType(), element.isNormalized(), vertexBuffer.getLayout().getStride(), element.getOffset());
            attribPointerIndex++;
        }
        glBindVertexArray(0);
    }

    @Override
    public void bind()
    {
        glBindVertexArray(vertexArrayID);
    }

    public void unbind()
    {
        glBindVertexArray(0);
    }

    @Override
    public void draw()
    {
        glDrawElements(GL_TRIANGLES, defaultIndices.length, GL_UNSIGNED_INT, 0);
    }

    @Override
    public void drawInstanced(int count) {}

    public void delete()
    {
        unbind();
        glDeleteVertexArrays(vertexArrayID);
        vertexBuffer.unbind();
        vertexBuffer.delete();
    }
}
