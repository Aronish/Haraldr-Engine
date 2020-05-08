package engine.graphics;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
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
    public int vertexAmount, indexAmount;
    private int indexBufferID;

    public VertexArray()
    {
        vertexArrayID = glCreateVertexArrays();
    }

    public void setIndexBuffer(@NotNull int[] indices)
    {
        indexAmount = indices.length;
        indexBufferID = glCreateBuffers();
        glBindVertexArray(vertexArrayID);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
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

    public void drawArrays()
    {
        glDrawArrays(GL_TRIANGLES, 0, vertexAmount);
    }

    public void drawElements()
    {
        glDrawElements(GL_TRIANGLES, indexAmount, GL_UNSIGNED_INT, 0);
    }

    /*public void drawElements(int drawMode)
    {
        glDrawElements(drawMode, indexAmount, GL_UNSIGNED_INT, 0);
    }*/

    public void drawElements(int indexAmount)
    {
        glDrawElements(GL_TRIANGLES, indexAmount, GL_UNSIGNED_INT, 0);

    }

    public void drawElementsInstanced(int count)
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
        glDeleteBuffers(indexBufferID);
        vertexBuffers.forEach(VertexBuffer::delete);
    }
}
