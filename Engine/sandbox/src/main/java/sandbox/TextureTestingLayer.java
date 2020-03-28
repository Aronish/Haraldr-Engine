package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.event.MouseMovedEvent;
import engine.event.MouseScrolledEvent;
import engine.graphics.DefaultModels;
import engine.graphics.DiffuseMaterial;
import engine.graphics.ForwardRenderer;
import engine.graphics.Model;
import engine.graphics.NormalMaterial;
import engine.graphics.PointLight;
import engine.graphics.Texture;
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
            "models/suzanne.obj",
            new NormalMaterial(
                    "default_textures/BricksPaintedWhite_COL_4K.jpg",
                    "default_textures/BricksPaintedWhite_NRM_4K.jpg"
            ),
            Matrix4f.rotate(new Vector3f(1f, 0f, 0f), 0f).multiply(Matrix4f.scale(new Vector3f(2f)))
    );

    private PointLight pointLight = new PointLight(new Vector3f(1f, 2f, 3f), new Vector3f(0.8f, 0.5f, 0.3f));
    private PointLight pointLight2 = new PointLight(new Vector3f(1f, 2f, 3f), new Vector3f(1f));

    private boolean showNormals;

    public TextureTestingLayer(String name)
    {
        super(name);
        renderer.getSceneLights().addPointLight(pointLight);
        renderer.getSceneLights().addPointLight(pointLight2);
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

    private Vector3f rotationAxis = new Vector3f(0f, 1f, 0f);
    private float rotation;

    @Override
    public void onUpdate(@NotNull Window window, float deltaTime)
    {
        if (window.isFocused())
        {
            perspectiveCamera.getController().handleMovement(perspectiveCamera, window.getWindowHandle(), deltaTime);
        }
        float sin = (float) Math.sin(Application.time / 2);
        float sinOff = (float) Math.sin(Application.time / 3 + 2f);
        float cos = (float) Math.cos(Application.time / 2);
        float cosOff = (float) Math.cos(Application.time / 3 + 2f);
        rotation += 25f * deltaTime;
        pointLight2.setPosition(new Vector3f(cos * 3f, sin * 3f, 2f));
        //model.setTransformationMatrix(Matrix4f.rotate(rotationAxis, rotation).multiply(Matrix4f.scale(new Vector3f(4f))));
    }

    private DiffuseMaterial material = new DiffuseMaterial(
            new Texture("default_textures/wood.png")
    );

    @Override
    public void onRender()
    {
        renderer.begin(perspectiveCamera);
        pointLight.render();
        pointLight2.render();
        model.render(renderer);
    }
}