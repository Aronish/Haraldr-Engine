package sandbox;

import engine.event.Event;
import engine.event.EventCategory;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.event.MouseMovedEvent;
import engine.event.MouseScrolledEvent;
import engine.graphics.Mesh;
import engine.graphics.ObjParser;
import engine.graphics.Renderer3D;
import engine.graphics.Shader;
import engine.input.Key;
import engine.layer.Layer;
import engine.main.Application;
import engine.main.PerspectiveCamera;
import engine.main.Window;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

import static engine.main.Application.MAIN_LOGGER;

public class ExampleLayer extends Layer
{
    private Shader objShader = new Shader("default_shaders/default3D");
    private Shader lightShader = new Shader("default_shaders/default3D.vert", "default_shaders/light.frag");
    private PerspectiveCamera perspectiveCamera = new PerspectiveCamera(new Vector3f(4f, 2f, -1f));

    private Mesh suzanne = ObjParser.load("models/suzanne.obj");
    private Mesh sphere = ObjParser.load("models/smooth_sphere.obj");

    private float sin, cos;

    public ExampleLayer(String name)
    {
        super(name);
    }

    @Override
    public void onEvent(@NotNull Window window, @NotNull Event event)
    {
        if (event.isInCategory(EventCategory.CATEGORY_WINDOW)) MAIN_LOGGER.info(event.toString());
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
        }
    }

    @Override
    public void onUpdate(@NotNull Window window, float deltaTime)
    {
        sin = (float) Math.sin(Application.time);
        cos = (float) Math.cos(Application.time);
        Renderer3D.light.setPosition(new Vector3f(cos * 3, 0f, sin * 3));
        if (window.isFocused())
        {
            perspectiveCamera.getController().handleMovement(perspectiveCamera, window.getWindowHandle(), deltaTime);
        }
    }

    @Override
    public void onRender()
    {
        Renderer3D.beginScene(perspectiveCamera);
        Renderer3D.drawCube(lightShader, Renderer3D.light.getPosition(), 0.25f);
        Renderer3D.drawCube(objShader, new Vector3f(-4f, 0f, 2f), new Vector3f(0.8f, 0.2f, 0.3f));
        Renderer3D.drawMesh(objShader, suzanne);
        Renderer3D.drawMesh(objShader, sphere, new Vector3f(0f, 3f, -3f));
    }
}
