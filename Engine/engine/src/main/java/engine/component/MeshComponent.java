package engine.component;

import engine.graphics.ResourceManager;
import engine.graphics.VertexArray;
import engine.graphics.material.Material;
import engine.math.Vector3f;

public class MeshComponent
{
    private VertexArray mesh;
    private Material material;

    public MeshComponent(String mesh, Material material)
    {
        this.mesh = ResourceManager.getMesh(mesh);
        this.material = material;
    }

    public void bind(Vector3f viewPosition)
    {
        material.bind();
        material.getShader().setVector3f(viewPosition, "viewPosition");
        mesh.bind();
    }

    public void render()
    {
        mesh.drawElements();
    }

    public Material getMaterial()
    {
        return material;
    }
}
