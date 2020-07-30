package haraldr.graphics;

import haraldr.main.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
    private final int target, usage;

    private VertexBufferLayout layout;
    private float[] data;
    private int vertexAmount;

    public VertexBuffer(@NotNull float[] data, @NotNull VertexBufferLayout layout, @NotNull Usage usage)
    {
        target = GL_ARRAY_BUFFER;
        this.usage = usage.VALUE;
        this.data = data;
        this.layout = layout;
        vertexAmount = data.length / layout.getVertexSize();
        vertexBufferID = glCreateBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
        glBufferData(GL_ARRAY_BUFFER, data, this.usage);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public VertexBuffer(int size, VertexBufferLayout layout, @NotNull Usage usage)
    {
        this.target = GL_ARRAY_BUFFER;
        this.usage = usage.VALUE;
        this.layout = layout;
        vertexBufferID = glCreateBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
        glBufferData(GL_ARRAY_BUFFER, size, this.usage);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public VertexBuffer(int target, int size, @NotNull Usage usage)
    {
        this.target = target;
        this.usage = usage.VALUE;
        vertexBufferID = glCreateBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
        glBufferData(GL_ARRAY_BUFFER, size, this.usage);
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

    public void setSubDataUnsafe(int[] data)
    {
        glBufferSubData(target, 0, data);
    }

    public void setSubDataUnsafe(float[] data)
    {
        glBufferSubData(target, 0, data);
    }

    public void setData(float[] data)
    {
        glBindBuffer(target, vertexBufferID);
        glBufferData(target, data, usage);
        glBindBuffer(target, 0);
    }

    public void setDataUnsafe(float[] data)
    {
        glBufferData(target, data, usage);
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

    public static int[] createQuadIndices(int amount)
    {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0, offset = 0; i < amount; ++i, offset += 4)
        {
            indices.add(offset);
            indices.add(3 + offset);
            indices.add(2 + offset);
            indices.add(offset);
            indices.add(2 + offset);
            indices.add(1 + offset);
        }
        return ArrayUtils.toPrimitiveArrayI(indices);
    }

    public enum Usage
    {
        STATIC_DRAW(GL_STATIC_DRAW),
        DYNAMIC_DRAW(GL_DYNAMIC_DRAW);

        public final int VALUE;

        Usage(int value)
        {
            VALUE = value;
        }
    }
}
