package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.event.MouseMovedEvent;
import engine.event.MouseScrolledEvent;
import engine.graphics.DefaultModels;
import engine.graphics.DirectionalLight;
import engine.graphics.ForwardRenderer;
import engine.graphics.Material;
import engine.graphics.Model;
import engine.graphics.PointLight;
import engine.graphics.Shader;
import engine.graphics.Spotlight;
import engine.input.Key;
import engine.layer.Layer;
import engine.main.Application;
import engine.main.PerspectiveCamera;
import engine.main.Window;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class TextureTestingLayer extends Layer
{
    private ForwardRenderer renderer = new ForwardRenderer();
    private PerspectiveCamera perspectiveCamera = new PerspectiveCamera(new Vector3f(0f, 2f, 2f));

    private Model model = new Model(
            DefaultModels.PLANE.mesh,
            new Material(
                    "default_textures/brickwall.jpg",
                    "default_textures/brickwall_normal.jpg",
                    Shader.NORMAL
            ),
            Matrix4f.scale(new Vector3f(4f, 4f, 1f))
    );

    private Model model2 = new Model(
            DefaultModels.PLANE.mesh,
            new Material(
                    "default_textures/brickwall.jpg",
                    "default_textures/brickwall_normal.jpg",
                    Shader.NORMAL
            ),
            Matrix4f.translate(new Vector3f(9f, 0f, 0f)).multiply(Matrix4f.scale(new Vector3f(4f, 4f, 1f)))
    );

    private PointLight pointLight = new PointLight(new Vector3f(4f, 0f, 3f), new Vector3f(0.8f, 0.2f, 0.3f));
    private Spotlight spotlight = new Spotlight(new Vector3f(4f, 0f, 2f), new Vector3f(0f, 0f, -1f), new Vector3f(0.8f, 0.2f, 0.3f), 25f, 25f);
    private DirectionalLight directionalLight = new DirectionalLight(new Vector3f(2f), new Vector3f(), new Vector3f(0.2f, 0.3f, 0.8f));

    private boolean showNormals;

    public TextureTestingLayer(String name)
    {
        super(name);
        renderer.getSceneLights().addDirectionalLight(directionalLight);
        renderer.getSceneLights().addPointLight(pointLight);
        renderer.getSceneLights().addSpotLight(spotlight);
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

    @Override
    public void onUpdate(@NotNull Window window, float deltaTime)
    {
        if (window.isFocused())
        {
            perspectiveCamera.getController().handleMovement(perspectiveCamera, window.getWindowHandle(), deltaTime);
        }
        float sin = (float) Math.sin(Application.time / 3);
        float sinOff = (float) Math.sin(Application.time / 3 + 2f);
        float cos = (float) Math.cos(Application.time / 3);
        float cosOff = (float) Math.cos(Application.time / 3 + 2f);
        spotlight.setDirection(new Vector3f(sin, 0f, cos));
        directionalLight.setDirection(new Vector3f(sinOff, 0f, cosOff));
    }

    @Override
    public void onRender()
    {
        renderer.begin(perspectiveCamera);
        pointLight.render();
        spotlight.render();
        spotlight.renderDirectionVector();
        directionalLight.render();
        directionalLight.renderDirectionVector();
        model.render(renderer);
        model2.render(renderer);
    }
}