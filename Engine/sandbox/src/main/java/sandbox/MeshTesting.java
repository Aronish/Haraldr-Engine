package sandbox;

import engine.event.Event;
import engine.graphics.Mesh;
import engine.graphics.ObjParser;
import engine.graphics.ResourceManager;
import engine.graphics.VertexArray;
import engine.layer.Layer;
import engine.main.Window;
import engine.math.Matrix4f;
import engine.math.Vector3f;

public class MeshTesting extends Layer
{
    private VertexArray vertexArray = ResourceManager.getMesh("models/cube.obj");
    private Mesh mesh = new Mesh(vertexArray.getVertexBuffers().get(0).getData(), vertexArray.getIndices());

    public MeshTesting(String name)
    {
        super(name);
    }

    @Override
    public void onEvent(Window window, Event event)
    {

    }

    @Override
    public void onUpdate(Window window, float deltaTime)
    {

    }

    @Override
    public void onRender()
    {

    }

    @Override
    public void onDispose()
    {

    }
}
