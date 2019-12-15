package engine.graphics;

import org.jetbrains.annotations.NotNull;

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
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL43.glMultiDrawElementsIndirect;
import static org.lwjgl.opengl.GL45.glCreateBuffers;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;

public class VertexArray
{
    //Winding order: Clockwise starting at top-left.
    private static final int[] defaultIndices = {
            0, 1, 2,
            0, 2, 3
    };

    private int vertexArrayID;
    private int nextAttribIndex = 0;
    private VertexBuffer vertexBuffer; //TODO Add support for multiple.

    public VertexArray()
    {
        this(defaultIndices);
    }

    public VertexArray(int[] indices)
    {
        vertexArrayID = glCreateVertexArrays();
        int indexBufferID = glCreateBuffers();
        glBindVertexArray(vertexArrayID);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        glBindVertexArray(0);
    }

    public void setVertexBuffer(@NotNull VertexBuffer vertexBuffer)
    {
        this.vertexBuffer = vertexBuffer;
        glBindVertexArray(vertexArrayID);
        vertexBuffer.bind();
        for (VertexBufferElement element : vertexBuffer.getLayout())
        {
            glEnableVertexAttribArray(nextAttribIndex);
            glVertexAttribPointer(nextAttribIndex, element.getSize(), element.getType(), element.isNormalized(), vertexBuffer.getLayout().getStride(), element.getOffset());
            ++nextAttribIndex;
        }
        glBindVertexArray(0);
    }

    public void bind()
    {
        glBindVertexArray(vertexArrayID);
    }

    public void unbind()
    {
        glBindVertexArray(0);
    }

    public void draw()
    {
        glDrawElements(GL_TRIANGLES, defaultIndices.length, GL_UNSIGNED_INT, 0);
    }

    public void drawInstanced(int count)
    {
        glDrawElementsInstanced(GL_TRIANGLES, defaultIndices.length, GL_UNSIGNED_INT, 0, count);
    }

    public void multiDrawIndirect(int count)
    {
        glMultiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, count, 0);
    }

    public void setNextAttribIndex(int index)
    {
        nextAttribIndex = index;
    }

    public int getNextAttribIndex()
    {
        return nextAttribIndex;
    }

    public VertexBuffer getVertexBuffer()
    {
        return vertexBuffer;
    }

    public void delete()
    {
        unbind();
        glDeleteVertexArrays(vertexArrayID);
        vertexBuffer.unbind();
        vertexBuffer.delete();
    }
}
