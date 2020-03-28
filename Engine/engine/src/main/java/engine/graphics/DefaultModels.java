package engine.graphics;

public enum DefaultModels
{
    SPHERE("models/sphere.obj"),
    CUBE("models/cube.obj"),
    PLANE("models/plane.obj");

    public final VertexArray mesh;

    DefaultModels(String modelPath)
    {
        mesh = ObjParser.loadMesh(modelPath);
    }

    public void bind()
    {
        mesh.bind();
    }

    public void drawElements()
    {
        mesh.drawElements();
    }
}
