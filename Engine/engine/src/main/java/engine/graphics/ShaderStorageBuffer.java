package engine.graphics;

import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

public class ShaderStorageBuffer
{
    private int shaderStorageBufferId;

    public ShaderStorageBuffer(int size)
    {
        shaderStorageBufferId = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, shaderStorageBufferId);
        glBufferData(GL_SHADER_STORAGE_BUFFER, size, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void setData(float[] data, int offset)
    {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, shaderStorageBufferId);
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, offset, data);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void setDataUnsafe(float[] data, int offset)
    {
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, offset, data);
    }

    public void bind(int bindingPoint)
    {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, bindingPoint, shaderStorageBufferId);
    }

    public void bindBuffer()
    {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, shaderStorageBufferId);
    }

    public void unbindBuffer()
    {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void delete()
    {
        glDeleteBuffers(shaderStorageBufferId);
    }
}
