package engine.graphics;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL40;

import static engine.main.Application.MAIN_LOGGER;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL45.glCreateBuffers;

@SuppressWarnings("unused")
public class VertexBuffer
{
    private final int vertexBufferID;
    private final int target;

    private VertexBufferLayout layout;
    private float[] data;
    private int vertexAmount;

    public VertexBuffer(@NotNull float[] data, @NotNull VertexBufferLayout layout, boolean dynamic)
    {
        target = GL_ARRAY_BUFFER;
        this.data = data;
        this.layout = layout;
        vertexAmount = data.length / layout.getVertexSize();
        vertexBufferID = glCreateBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
        glBufferData(GL_ARRAY_BUFFER, data, dynamic ? GL_DYNAMIC_DRAW : GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public VertexBuffer(int size, VertexBufferLayout layout, boolean dynamic)
    {
        this.target = GL_ARRAY_BUFFER;
        this.layout = layout;
        vertexBufferID = glCreateBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
        glBufferData(GL_ARRAY_BUFFER, size, dynamic ? GL_DYNAMIC_DRAW : GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public VertexBuffer(int target, int size, boolean dynamic)
    {
        this.target = target;
        vertexBufferID = glCreateBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
        glBufferData(GL_ARRAY_BUFFER, size, dynamic ? GL_DYNAMIC_DRAW : GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public static void bind(int target, int buffer)
    {
        glBindBuffer(target, buffer);
    }

    public void bind()
    {
        glBindBuffer(target, vertexBufferID);
    }

    public void unbind()
    {
        glBindBuffer(target, 0);
    }

    public void setData(int[] data)
    {
        glBufferSubData(target, 0, data);
    }

    public void setData(float[] data)
    {
        glBufferSubData(target, 0, data);
    }

    public float[] getData()
    {
        return data;
    }

    public VertexBufferLayout getLayout()
    {
        return layout;
    }

    public int getVertexAmount()
    {
        return vertexAmount;
    }

    public void delete()
    {
        glDeleteBuffers(vertexBufferID);
    }
}
