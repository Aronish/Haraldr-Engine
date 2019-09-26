package com.game.graphics;

import com.game.physics.AABB;

import static com.game.Application.MAIN_LOGGER;

public class ModelImpr implements IModel
{
    private VertexArrayImpr vertexArray;
    private AABB aabb;

    ModelImpr(float[] data, VertexBufferLayout layout, float width, float height)
    {
        setVertexArray(data, layout);
        aabb = new AABB(width, height);
    }

    private void setVertexArray(float[] data, VertexBufferLayout layout)
    {
        vertexArray = new VertexArrayImpr();
        vertexArray.bind();
        VertexBuffer vertexBuffer = new VertexBuffer(data, layout);
        vertexArray.setVertexBuffer(vertexBuffer);
        vertexArray.unbind();
    }

    @Override
    public VertexArrayImpr getVertexArray()
    {
        return vertexArray;
    }

    public AABB getAABB()
    {
        return aabb;
    }

    @Override
    public void dispose(){
        vertexArray.unbind();
        vertexArray.delete();
    }

    @Override
    public void printType() {
        MAIN_LOGGER.info("IMPR");
    }
}
