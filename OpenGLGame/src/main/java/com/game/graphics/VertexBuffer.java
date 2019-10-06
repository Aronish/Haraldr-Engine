package com.game.graphics;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL45.glCreateBuffers;

public class VertexBuffer
{
    public int vertexBufferID;
    private VertexBufferLayout layout;
    private float[] data;

    public VertexBuffer(float[] data, VertexBufferLayout layout)
    {
        this.data = data;
        this.layout = layout;
        vertexBufferID = glCreateBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public VertexBuffer(int size, VertexBufferLayout layout)
    {
        this.layout = layout;
        vertexBufferID = glCreateBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
        glBufferData(GL_ARRAY_BUFFER, size, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void bind()
    {
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
    }

    public void unbind()
    {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
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
