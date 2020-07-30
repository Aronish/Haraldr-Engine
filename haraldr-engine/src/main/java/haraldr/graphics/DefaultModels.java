package haraldr.graphics;

public enum DefaultModels
{
    SPHERE("default_meshes/sphere.obj"),
    CUBE("default_meshes/cube.obj"),
    PLANE("default_meshes/plane.obj");

    public final VertexArray mesh;

    DefaultModels(String path)
    {
        mesh = ResourceManager.getMesh(path);
    }

    public void bind()
    {
        mesh.bind();
    }

    public void drawElements()
    {
        mesh.drawElements();
    }

    public void delete()
    {
        mesh.delete();
    }
}
