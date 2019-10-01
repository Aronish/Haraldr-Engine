package com.game.graphics;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL45.glCreateBuffers;

public class VertexBuffer
{
    private int vertexBufferID;
    private VertexBufferLayout layout;

    public VertexBuffer(float[] data, VertexBufferLayout layout)
    {
        this.layout = layout;
        vertexBufferID = glCreateBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
    }

    public VertexBuffer(int size, VertexBufferLayout layout)
    {
        this.layout = layout;
        vertexBufferID = glCreateBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
        glBufferData(GL_ARRAY_BUFFER, size, GL_STATIC_DRAW);
    }

    public void bind()
    {
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
    }

    public void unbind()
    {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
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
