package engine.graphics;

public class Mesh //NEEDED?
{
    private VertexArray vertexArray;

    public Mesh(VertexArray vertexArray)
    {
        this.vertexArray = vertexArray;
    }

    public void bind()
    {
        vertexArray.bind();
    }

    public void unbind()
    {
        vertexArray.unbind();
    }

    public void draw()
    {
        vertexArray.drawElements();
    }
}