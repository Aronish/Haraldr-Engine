package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.event.MouseMovedEvent;
import engine.event.MouseScrolledEvent;
import engine.graphics.DefaultModels;
import engine.graphics.ForwardRenderer;
import engine.graphics.Model;
import engine.graphics.material.NormalMaterial;
import engine.graphics.lighting.PointLight;
import engine.graphics.lighting.SceneLights;
import engine.graphics.lighting.Spotlight;
import engine.input.Button;
import engine.input.Input;
import engine.layer.Layer;
import engine.main.Application;
import engine.main.PerspectiveCamera;
import engine.main.Window;
import engine.math.Matrix4f;
import engine.math.Vector2f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class TextureTestingLayer extends Layer
{
    private final ForwardRenderer renderer = new ForwardRenderer();
    private final PerspectiveCamera perspectiveCamera = new PerspectiveCamera(new Vector3f(0f, 2f, 2f));

    private final Model wall = new Model(
            DefaultModels.PLANE.mesh,
            new NormalMaterial(
                    "default_textures/BricksPaintedWhite_COL_4K.jpg",
                    "default_textures/BricksPaintedWhite_NRM_4K.jpg"
            ),
            Matrix4f.translate(new Vector3f(5f, 5f, -5f)).multiply(Matrix4f.scale(new Vector2f(10f)))
    );

    private final Model model = new Model(
            "models/suzanne.obj",
            new NormalMaterial(
                    "default_textures/BricksPaintedWhite_COL_4K.jpg",
                    "default_textures/BricksPaintedWhite_NRM_4K.jpg"
            ),
            Matrix4f.translate(new Vector3f(0f, 4f, 0f)).multiply(Matrix4f.scale(new Vector3f(1f)))
    );

    private final Spotlight flashLight = new Spotlight(perspectiveCamera.getPosition(), perspectiveCamera.getDirection(), new Vector3f(1f), 20f, 25f);
    private final PointLight pointLight = new PointLight(new Vector3f(5f, 5f, -4f), new Vector3f(1f));

    private final SceneLights sceneLights = new SceneLights();

    public TextureTestingLayer(String name)
    {
        super(name);
        for (int i = 0; i < 10; ++i)
        {
            sceneLights.addLight(new PointLight(
                    new Vector3f(Math.cos(i * (Math.PI / 5f)) * 1.8f, 4f, Math.sin(i * (Math.PI / 5f)) * 1.8f),
                    new Vector3f(Math.random(), Math.random(), 1f - Math.random()), 1.0f, 0.7f, 1.8f)
            );
        }
        sceneLights.addLight(flashLight);
        sceneLights.addLight(pointLight);
        renderer.setSceneLights(sceneLights);
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
        if (event.eventType == EventType.KEY_PRESSED)
        {
            EventHandler.onKeyPress((KeyPressedEvent) event, window);
        }
        if (event.eventType == EventType.MOUSE_SCROLLED)
        {
            perspectiveCamera.getController().handleScroll((MouseScrolledEvent) event);
        }
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            if (Input.wasMouseButton(event, Button.MOUSE_BUTTON_1))
            {
                flashLight.setColor(new Vector3f(0f));
            }
            if (Input.wasMouseButton(event, Button.MOUSE_BUTTON_2))
            {
                flashLight.setColor(new Vector3f(1f));
            }
        }
    }

    private final Vector3f rotationAxis = new Vector3f(0f, 1f, 0f);
    private float rotation;

    @Override
    public void onUpdate(@NotNull Window window, float deltaTime)
    {
        if (window.isFocused())
        {
            perspectiveCamera.getController().handleMovement(perspectiveCamera, window.getWindowHandle(), deltaTime);
        }
        float sin = (float) Math.sin(Application.time / 2);
        float cos = (float) Math.cos(Application.time / 2);
        rotation += 10f * deltaTime;
        flashLight.setDirection(perspectiveCamera.getDirection());
        flashLight.setPosition(perspectiveCamera.getPosition());
        pointLight.setPosition(new Vector3f(cos * 2f + 2f, sin * 2f + 2f, -2f));
    }

    @Override
    public void onRender()
    {
        renderer.begin(perspectiveCamera);
        model.render(renderer);
        wall.render(renderer);
        pointLight.render();
    }

    @Override
    public void onDispose()
    {
        model.delete();
    }
}