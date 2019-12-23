package engine.graphics;

import engine.physics.AABB;

public class Model
{
    //Winding order: Clockwise starting at top-left.
    private static final int[] quadIndices = {
            0, 1, 2,
            0, 2, 3
    };

    private VertexArray vertexArray;
    private AABB aabb;

    public Model(float[] data, VertexBufferLayout layout, float width, float height)
    {
        setVertexArray(data, layout);
        aabb = new AABB(width, height);
    }

    private void setVertexArray(float[] data, VertexBufferLayout layout)
    {
        vertexArray = new VertexArray(quadIndices);
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
