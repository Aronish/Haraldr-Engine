package engine.graphics;

import org.lwjgl.opengl.GL40;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL45.glCreateBuffers;

public class VertexBuffer
{
    public static final int GL_DRAW_INDIRECT_BUFFER = GL40.GL_DRAW_INDIRECT_BUFFER;

    private final int vertexBufferID;
    private final int target;
    private VertexBufferLayout layout;
    private float[] data;

    public VertexBuffer(float[] data, VertexBufferLayout layout, boolean dynamic)
    {
        this.target = GL_ARRAY_BUFFER;
        this.layout = layout;
        this.data = data;
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

    public void bind()
    {
        glBindBuffer(target, vertexBufferID);
    }

    public static void bind(int target, int buffer)
    {
        glBindBuffer(target, buffer);
    }

    public void unbind()
    {
        glBindBuffer(target, 0);
    }

    //Good for decoupling low level libraries from clients. No idea if you should do it like this.
    //Could also have something that works like this:
    /*
    public static void bind(VertexBuffer buffer) { glBindBuffer(buffer.rendererId); }
    */
    public void setData(float[] data)
    {
        glBufferSubData(target, 0, data);
    }

    public void setData(int[] data)
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

    public void delete()
    {
        glDeleteBuffers(vertexBufferID);
    }
}
