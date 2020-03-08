package engine.graphics;

public enum DefaultModels
{
    CUBE("models/cube.obj"),
    PLANE("models/plane.obj");

    public final Mesh mesh;

    DefaultModels(String modelPath)
    {
        mesh = new Mesh(ObjParser.load(modelPath));
    }
}
