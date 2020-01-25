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
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import static org.lwjgl.opengl.GL43.glMultiDrawElementsIndirect;
import static org.lwjgl.opengl.GL45.glCreateBuffers;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;

@SuppressWarnings("WeakerAccess")
public class VertexArray
{
    private int vertexArrayID;
    private int nextAttribIndex = 0;
    private VertexBuffer vertexBuffer; //TODO Add support for multiple.
    private int numIndices;

    public VertexArray()
    {
        vertexArrayID = glCreateVertexArrays();
    }

    public VertexArray(@NotNull int[] indices)
    {
        this();
        numIndices = indices.length;
        glBindVertexArray(vertexArrayID);
        setIndexBuffer(indices);
        glBindVertexArray(0);
    }

    public void setIndexBuffer(@NotNull int[] indices)
    {
        numIndices = indices.length;
        int indexBufferID = glCreateBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
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

    public static void enableVertexAttribArrayWrapper(int attribIndex)
    {
        glEnableVertexAttribArray(attribIndex);
    }

    public static void vertexAttribPointer(int attribIndex, @NotNull VertexBufferElement element, int stride)
    {
        glVertexAttribPointer(attribIndex, element.getSize(), element.getType(), element.isNormalized(), stride, element.getOffset());
    }

    public static void vertexAttribDivisor(int attribIndex, int divisor)
    {
        glVertexAttribDivisor(attribIndex, divisor);
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
        glDrawElements(GL_TRIANGLES, numIndices, GL_UNSIGNED_INT, 0);
    }

    public void drawInstanced(int count)
    {
        glDrawElementsInstanced(GL_TRIANGLES, numIndices, GL_UNSIGNED_INT, 0, count);
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
