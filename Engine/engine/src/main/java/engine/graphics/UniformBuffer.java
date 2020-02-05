package engine.graphics;

import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindBufferRange;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

public class UniformBuffer
{
    private int uniformBufferId;
    private int size;

    public UniformBuffer(int size)
    {
        this.size = size;
        uniformBufferId = glGenBuffers();
        glBindBuffer(GL_UNIFORM_BUFFER, uniformBufferId);
        glBufferData(GL_UNIFORM_BUFFER, size, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
        glBindBufferRange(GL_UNIFORM_BUFFER, 0, uniformBufferId, 0, size);
    }

    public void setData(float[] data, int offset)
    {
        glBindBuffer(GL_UNIFORM_BUFFER, uniformBufferId);
        glBufferSubData(GL_UNIFORM_BUFFER, offset, data);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }

    public void bind()
    {
        glBindBufferRange(GL_UNIFORM_BUFFER, 0, uniformBufferId, 0, size);
    }
}
