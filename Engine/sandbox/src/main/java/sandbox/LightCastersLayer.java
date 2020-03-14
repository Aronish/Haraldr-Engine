package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.event.MouseMovedEvent;
import engine.event.MouseScrolledEvent;
import engine.graphics.ForwardRenderer;
import engine.graphics.Light;
import engine.graphics.PointLight;
import engine.graphics.Shader;
import engine.input.Key;
import engine.layer.Layer;
import engine.main.Application;
import engine.main.PerspectiveCamera;
import engine.main.Window;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class LightCastersLayer extends Layer
{
    private ForwardRenderer renderer = new ForwardRenderer();
    private PerspectiveCamera perspectiveCamera = new PerspectiveCamera(new Vector3f(4f, 2f, -1f));

    private Light light = new PointLight(new Vector3f(-10f, 0f, 0f), new Vector3f(1.0f, 1.0f, 0.85f));
    private Light light2 = new PointLight(new Vector3f(-10f, 0f, 0f), new Vector3f(0.4f, 0.0f, 0.9f));

    public LightCastersLayer(String name)
    {
        super(name);
        renderer.getSceneLights().addLight(light);
        renderer.getSceneLights().addLight(light2);
    }

    @Override
    public void onEvent(Window window, @NotNull Event event)
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

    private float sin, cos, sinOff, cosOff;

    @Override
    public void onUpdate(@NotNull Window window, float deltaTime)
    {
        if (window.isFocused())
        {
            perspectiveCamera.getController().handleMovement(perspectiveCamera, window.getWindowHandle(), deltaTime);
        }
        rotation += 100f * deltaTime;
        sin = (float) Math.sin(Application.time / 3f);
        cos = (float) Math.cos(Application.time / 3f);
        sinOff = (float) Math.sin((Application.time + 10f) / 3f);
        cosOff = (float) Math.cos((Application.time + 10f) / 3f);
        light.setPosition(new Vector3f(sin * 3f, 0f, cos * 3f));
        light2.setPosition(new Vector3f(-4f,cosOff * 3f, sinOff * 3f + 2f));
    }

    private float rotation;

    private Vector3f[] rotationAxes = {
            new Vector3f(2f, 4f, 0f),
            new Vector3f(1f, 2f, 3f),
            new Vector3f(8f, 3f, 0f),
            new Vector3f(0f, 0f, 2f)
    };

    @Override
    public void onRender()
    {
        renderer.begin(perspectiveCamera);
        light.render(renderer);
        light2.render(renderer);
        renderer.drawCube(new Vector3f(1f, 2f, 3f), 1f, rotationAxes[0], rotation);
        renderer.drawCube(new Vector3f(-3, 5f, 5f), 1f, rotationAxes[1], rotation);
        renderer.drawCube(new Vector3f(4f, 0f, -1f), 1f, rotationAxes[2], rotation);
        renderer.drawCube(new Vector3f(-7f, 0f, 3f), 1f, rotationAxes[3], rotation);
    }
}
