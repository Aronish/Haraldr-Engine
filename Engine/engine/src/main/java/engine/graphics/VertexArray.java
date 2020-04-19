package engine.graphics;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL43.glMultiDrawElementsIndirect;
import static org.lwjgl.opengl.GL45.glCreateBuffers;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;

@SuppressWarnings("unused")
public class VertexArray
{
    private final int vertexArrayID;
    private final List<VertexBuffer> vertexBuffers = new ArrayList<>();
    private int nextAttribIndex;
    private int vertexAmount, indexAmount;
    private int indexBuffer;
    private int[] indices;

    public VertexArray()
    {
        vertexArrayID = glCreateVertexArrays();
    }

    public VertexArray(int size)
    {
        vertexArrayID = glCreateVertexArrays();
        indexBuffer = glCreateBuffers();
        glBindVertexArray(vertexArrayID);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);
        glBindVertexArray(0);
    }

    public void setIndexBuffer(@NotNull int[] indices)
    {
        indexAmount = indices.length;
        this.indices = indices;
        int indexBufferID = glCreateBuffers();
        glBindVertexArray(vertexArrayID);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        glBindVertexArray(0);
    }

    public void setIndicesUnsafe(@NotNull int[] indices)
    {
        indexAmount = indices.length;
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, indices);
    }

    public void setVertexBuffers(@NotNull VertexBuffer... vertexBuffers)
    {
        glBindVertexArray(vertexArrayID);
        for (VertexBuffer vertexBuffer : vertexBuffers)
        {
            this.vertexBuffers.add(vertexBuffer);
            vertexBuffer.bind();
            for (VertexBufferElement element : vertexBuffer.getLayout())
            {
                glEnableVertexAttribArray(nextAttribIndex);
                glVertexAttribPointer(nextAttribIndex, element.getSize(), element.getType(), element.isNormalized(), vertexBuffer.getLayout().getStride(), element.getOffset());
                ++nextAttribIndex;
            }
            vertexAmount += vertexBuffer.getVertexAmount();
            glBindVertexArray(0);
        }
    }

    public void bind()
    {
        glBindVertexArray(vertexArrayID);
    }

    public void unbind()
    {
        glBindVertexArray(0);
    }

    public void drawArrays()
    {
        glDrawArrays(GL_TRIANGLES, 0, vertexAmount);
    }

    public void drawElements()
    {
        glDrawElements(GL_TRIANGLES, indexAmount, GL_UNSIGNED_INT, 0);
    }

    public void drawElements(int drawMode)
    {
        glDrawElements(drawMode, indexAmount, GL_UNSIGNED_INT, 0);
    }

    public void drawInstanced(int count)
    {
        glDrawElementsInstanced(GL_TRIANGLES, indexAmount, GL_UNSIGNED_INT, 0, count);
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

    public List<VertexBuffer> getVertexBuffers()
    {
        return vertexBuffers;
    }

    public int[] getIndices()
    {
        return indices;
    }

    public void delete()
    {
        unbind();
        glDeleteVertexArrays(vertexArrayID);
        vertexBuffers.forEach(VertexBuffer::delete);
    }
}
