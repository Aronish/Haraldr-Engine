package graphics;

import physics.AABB;

public class Model
{
    private VertexArray vertexArray;
    private AABB aabb;

    Model(float[] data, VertexBufferLayout layout, float width, float height)
    {
        setVertexArray(data, layout);
        aabb = new AABB(width, height);
    }

    private void setVertexArray(float[] data, VertexBufferLayout layout)
    {
        vertexArray = new VertexArray();
        VertexBuffer vertexBuffer = new VertexBuffer(data, layout, false);
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

    public void dispose(){
        vertexArray.unbind();
        vertexArray.delete();
    }
}
