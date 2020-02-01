package engine.graphics;

public class Mesh
{
    private VertexArray vertexArray;
    private Material material;

    public Mesh(VertexArray vertexArray)
    {
        this.vertexArray = vertexArray;
    }

    public void setMaterial(Material material)
    {
        this.material = material;
    }

    public VertexArray getVertexArray()
    {
        return vertexArray;
    }

    public Material getMaterial()
    {
        return material;
    }
}