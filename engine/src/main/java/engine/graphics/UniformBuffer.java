package engine.graphics;

import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL45.glCreateBuffers;

public class UniformBuffer
{
    private int uniformBufferId;

    public UniformBuffer(int size)
    {
        uniformBufferId = glCreateBuffers();
        glBindBuffer(GL_UNIFORM_BUFFER, uniformBufferId);
        glBufferData(GL_UNIFORM_BUFFER, size, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }

    public void setData(float[] data, int offset)
    {
        glBindBuffer(GL_UNIFORM_BUFFER, uniformBufferId);
        glBufferSubData(GL_UNIFORM_BUFFER, offset, data);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }

    public void setDataUnsafe(float[] data, int offset)
    {
        glBufferSubData(GL_UNIFORM_BUFFER, offset, data);
    }

    public void bind(int bindingPoint)
    {
        glBindBufferBase(GL_UNIFORM_BUFFER, bindingPoint, uniformBufferId);
    }

    public void bindBuffer()
    {
        glBindBuffer(GL_UNIFORM_BUFFER, uniformBufferId);
    }

    public void unbindBuffer()
    {
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }

    public void delete()
    {
        glDeleteBuffers(uniformBufferId);
    }
}