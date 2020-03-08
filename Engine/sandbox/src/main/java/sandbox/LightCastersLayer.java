package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.event.MouseMovedEvent;
import engine.event.MouseScrolledEvent;
import engine.graphics.ForwardRenderer;
import engine.graphics.Light;
import engine.graphics.Shader;
import engine.input.Key;
import engine.layer.Layer;
import engine.main.PerspectiveCamera;
import engine.main.Window;
import engine.math.Vector3f;

public class LightCastersLayer extends Layer
{
    private ForwardRenderer renderer = new ForwardRenderer();
    private PerspectiveCamera perspectiveCamera = new PerspectiveCamera(new Vector3f(4f, 2f, -1f));

    private Light light = new Light(new Vector3f(-10f, 0f, 0f), new Vector3f(1.0f, 1.0f, 0.85f));

    public LightCastersLayer(String name)
    {
        super(name);
        renderer.getSceneLights().addLight(light);
    }

    @Override
    public void onEvent(Window window, Event event)
    {
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            if (window.isFocused())
            {
                perspectiveCamera.getController().handleRotation(perspectiveCamera, (MouseMovedEvent) event);
            }
        }
        if (event.eventType == EventType.MOUSE_SCROLLED)
        {
            perspectiveCamera.getController().handleScroll((MouseScrolledEvent) event);
        }
        if (event.eventType == EventType.KEY_PRESSED)
        {
            EventHandler.onKeyPress((KeyPressedEvent) event, window);
            if (((KeyPressedEvent) event).keyCode == Key.KEY_Q.keyCode)
            {
                Shader.DIFFUSE.recompile();
            }
        }
    }

    @Override
    public void onUpdate(Window window, float deltaTime)
    {
        if (window.isFocused())
        {
            perspectiveCamera.getController().handleMovement(perspectiveCamera, window.getWindowHandle(), deltaTime);
        }
    }

    @Override
    public void onRender()
    {
        renderer.begin(perspectiveCamera);
        light.render(renderer);
        renderer.drawCube(new Vector3f(1f, 2f, 3f));
        renderer.drawCube(new Vector3f(-3, 5f, 5f));
        renderer.drawCube(new Vector3f(4f, -5f, -1f));
        renderer.drawCube(new Vector3f(-7f, 0f, 3f));
    }
}
