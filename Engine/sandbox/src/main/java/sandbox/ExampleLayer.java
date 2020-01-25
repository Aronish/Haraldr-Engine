package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.MouseMovedEvent;
import engine.graphics.Renderer3D;
import engine.layer.Layer;
import engine.main.Application;
import engine.main.PerspectiveCamera;
import engine.main.Window;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class ExampleLayer extends Layer
{
    private PerspectiveCamera perspectiveCamera = new PerspectiveCamera(new Vector3f(0f, 0f, -5f));

    public ExampleLayer(String name)
    {
        super(name);
        Matrix4f.perspective.print();
    }

    @Override
    public void onEvent(@NotNull Window window, @NotNull Event event)
    {
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            perspectiveCamera.getController().handleRotation(perspectiveCamera, (MouseMovedEvent) event, window);
        }
    }

    @Override
    public void onUpdate(@NotNull Window window, float deltaTime)
    {
        perspectiveCamera.getController().handleMovement(perspectiveCamera, window.getWindowHandle(), deltaTime);
    }

    @Override
    public void onRender()
    {
        Renderer3D.beginScene(perspectiveCamera);
        Renderer3D.drawCube(new Vector3f(0f, 0f, 0f));
        Renderer3D.drawCube(new Vector3f(2f, 5f, 3f));
    }
}
