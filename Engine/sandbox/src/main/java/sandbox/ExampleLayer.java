package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.event.MouseMovedEvent;
import engine.event.MouseScrolledEvent;
import engine.graphics.Renderer3D;
import engine.graphics.Shader;
import engine.input.Key;
import engine.layer.Layer;
import engine.main.PerspectiveCamera;
import engine.main.Window;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class ExampleLayer extends Layer
{
    private Shader objShader = new Shader("default_shaders/default3D.vert", "default_shaders/default3D.frag");
    private Shader lightShader = new Shader("default_shaders/default3D.vert", "default_shaders/simpleColor.frag");
    private PerspectiveCamera perspectiveCamera = new PerspectiveCamera(new Vector3f(4f, 2f, -1f));

    private boolean showNormals;

    public ExampleLayer(String name)
    {
        super(name);
    }

    @Override
    public void onEvent(@NotNull Window window, @NotNull Event event)
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
                lightShader.recompile();
                objShader.recompile();
            }
            if (((KeyPressedEvent) event).keyCode == Key.KEY_N.keyCode)
            {
                showNormals = !showNormals;
            }
        }
    }

    @Override
    public void onUpdate(@NotNull Window window, float deltaTime)
    {
        if (window.isFocused())
        {
            perspectiveCamera.getController().handleMovement(perspectiveCamera, window.getWindowHandle(), deltaTime);
        }
    }

    @Override
    public void onRender()
    {
        Renderer3D.beginScene(perspectiveCamera);
        Renderer3D.drawCube(lightShader, Renderer3D.light.getPosition(), 0.0625f, Renderer3D.light.getColor());
        Renderer3D.drawCube(objShader, new Vector3f(), new Vector3f(0.8f, 0.2f, 0.3f));
        Renderer3D.drawVector(Renderer3D.rotationAxis, new Vector3f());
    }
}