package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.event.MouseMovedEvent;
import engine.event.MouseScrolledEvent;
import engine.graphics.DefaultModels;
import engine.graphics.ForwardRenderer;
import engine.graphics.Light;
import engine.graphics.Material;
import engine.graphics.Model;
import engine.graphics.Renderer3D;
import engine.graphics.Shader;
import engine.input.Key;
import engine.layer.Layer;
import engine.main.Application;
import engine.main.PerspectiveCamera;
import engine.main.Window;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class ExampleLayer extends Layer
{
    private Shader shader = new Shader("default_shaders/normal.vert",  "default_shaders/normal.frag");
    private Shader lightShader = new Shader("default_shaders/default3D.vert", "default_shaders/simpleColor.frag");
    //private Material material = new Material("default_textures/BricksPaintedWhite001_COL_4K.jpg", "default_textures/BricksPaintedWhite001_NRM_4K.jpg", shader);
    private Material material = new Material("default_textures/brickwall.jpg", "default_textures/brickwall_normal.jpg", shader);
    private Model model = new Model(DefaultModels.PLANE.mesh, material);

    private ForwardRenderer renderer = new ForwardRenderer();
    private static Light light = new Light(new Vector3f(2f, 2f, 1f), new Vector3f(0.4f, 0.2f, 0.85f));
    private static Light light2 = new Light(new Vector3f(-2f, -2f, 1f), new Vector3f(0.2f, 0.6f, 0.2f));

    private PerspectiveCamera perspectiveCamera = new PerspectiveCamera(new Vector3f(4f, 2f, -1f));

    private boolean showNormals;

    public ExampleLayer(String name)
    {
        super(name);
        renderer.sceneLights.addLight(light);
        renderer.sceneLights.addLight(light2);
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
            }
            if (((KeyPressedEvent) event).keyCode == Key.KEY_N.keyCode)
            {
                showNormals = !showNormals;
            }
        }
    }

    private float sin, sinOff, cos, cosOff;

    @Override
    public void onUpdate(@NotNull Window window, float deltaTime)
    {
        if (window.isFocused())
        {
            perspectiveCamera.getController().handleMovement(perspectiveCamera, window.getWindowHandle(), deltaTime);
        }
        sin     = (float) Math.sin(Application.time / 3) * 2f;
        sinOff  = (float) Math.sin(Application.time / 3 + 2f) * 2f;
        cos     = (float) Math.cos(Application.time / 3) * 2f;
        cosOff  = (float) Math.cos(Application.time / 3 + 2f) * 2f;
        light.setPosition(new Vector3f(sin, cos, 1f));
        light2.setPosition(new Vector3f(sinOff, cosOff, 1f));
    }

    @Override
    public void onRender()
    {
        Renderer3D.beginScene(perspectiveCamera);
        Renderer3D.drawCube(lightShader, light.getPosition(), 0.0625f, light.getColor());
        Renderer3D.drawCube(lightShader, light2.getPosition(), 0.0625f, light2.getColor());
        model.render(renderer, perspectiveCamera);
    }
}