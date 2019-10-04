package com.game.graphics;

import com.game.physics.AABB;

public class Model
{
    private VertexArray vertexArray;
    private AABB aabb;
    private float[] textureCoordinates;

    Model(float[] data, float[] textureCoordinates, VertexBufferLayout layout, float width, float height)
    {
        this.textureCoordinates = textureCoordinates;
        setVertexArray(data, layout);
        aabb = new AABB(width, height);
    }

    private void setVertexArray(float[] data, VertexBufferLayout layout)
    {
        vertexArray = new VertexArray();
        VertexBuffer vertexBuffer = new VertexBuffer(data, layout);
        vertexArray.setVertexBuffer(vertexBuffer);
    }

    public VertexArray getVertexArray()
    {
        return vertexArray;
    }

    public AABB getAABB()
    {
        return aabb;
    }

    public float[] getTextureCoordinates()
    {
        return textureCoordinates;
    }

    public void dispose(){
        vertexArray.unbind();
        vertexArray.delete();
    }
}
