package engine.graphics;

import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

public class UniformBuffer
{
    private int uniformBufferId;
    private int size;
    private float[] data;

    public UniformBuffer(int size)
    {
        this.size = size;
        uniformBufferId = glGenBuffers();
        glBindBuffer(GL_UNIFORM_BUFFER, uniformBufferId);
        glBufferData(GL_UNIFORM_BUFFER, size, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }

    public void setData(float[] data, int offset)
    {
        this.data = data;
        glBindBuffer(GL_UNIFORM_BUFFER, uniformBufferId);
        glBufferSubData(GL_UNIFORM_BUFFER, offset, data);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }

    public void setDataUnsafe(float[] data, int offset)
    {
        this.data = data;
        glBufferSubData(GL_UNIFORM_BUFFER, offset, data);
    }

    public void bind(int bindingPoint)
    {
        glBindBufferBase(GL_UNIFORM_BUFFER, bindingPoint, uniformBufferId);
    }
}