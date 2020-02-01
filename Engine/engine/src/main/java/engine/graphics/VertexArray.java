package engine.graphics;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL43.glMultiDrawElementsIndirect;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;

@SuppressWarnings("WeakerAccess")
public class VertexArray
{
    private int vertexArrayID;
    private int nextAttribIndex;
    private List<VertexBuffer> vertexBuffers = new ArrayList<>();
    private IndexBuffer indexBuffer;
    private int vertexAmount, indexAmount;

    public VertexArray()
    {
        vertexArrayID = glCreateVertexArrays();
    }

    public void setIndexBuffer(@NotNull int[] indices)
    {
        indexAmount = indices.length;
        glBindVertexArray(vertexArrayID);
        indexBuffer = new IndexBuffer(indices);
        glBindVertexArray(0);
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

    public void drawElements()
    {
        glDrawElements(GL_TRIANGLES, indexAmount, GL_UNSIGNED_INT, 0);
    }

    public void drawArrays()
    {
        glDrawArrays(GL_TRIANGLES, 0, vertexAmount);
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

    public void delete()
    {
        unbind();
        glDeleteVertexArrays(vertexArrayID);
        vertexBuffers.forEach(VertexBuffer::delete);
    }
}
