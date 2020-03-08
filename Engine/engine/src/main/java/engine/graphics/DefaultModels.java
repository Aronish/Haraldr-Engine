package engine.graphics;

public enum DefaultModels
{
    CUBE("models/cube.obj"),
    PLANE("models/plane.obj");

    public final VertexArray mesh;

    DefaultModels(String modelPath)
    {
        mesh = ObjParser.load(modelPath);
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
