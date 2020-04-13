package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.event.MouseMovedEvent;
import engine.event.MousePressedEvent;
import engine.event.MouseScrolledEvent;
import engine.graphics.DefaultModels;
import engine.graphics.DiffuseMaterial;
import engine.graphics.ForwardRenderer;
import engine.graphics.Model;
import engine.graphics.NormalMaterial;
import engine.graphics.PointLight;
import engine.graphics.Spotlight;
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
                    "default_textures/brickwall.jpg",
                    "default_textures/brickwall_normal.jpg"
            ),
            Matrix4f.translate(new Vector3f(5f, 5f, -5f)).multiply(Matrix4f.scale(new Vector2f(10f)))
    );

    private final Model model = new Model(
            "models/suzanne.obj",
            new NormalMaterial(
                    "default_textures/BricksPaintedWhite_COL_4K.jpg",
                    "default_textures/BricksPaintedWhite_NRM_4K.jpg"
            ),
            Matrix4f.translate(new Vector3f(0f, 1.8f, 0f)).multiply(Matrix4f.rotate(new Vector3f(1f, 1f, 0f), 60f).multiply(Matrix4f.scale(new Vector3f(0.2f))))
    );

    private final Model model2 = new Model(
            "models/suzanne.obj",
            DiffuseMaterial.DEFAULT,
            Matrix4f.translate(new Vector3f(0f, 1.8f, 0f)).multiply(Matrix4f.rotate(new Vector3f(1f, 1f, 0f), 60f).multiply(Matrix4f.scale(new Vector3f(0.2f))))
    );

    private final Spotlight flashLight = new Spotlight(perspectiveCamera.getPosition(), perspectiveCamera.getDirection(), new Vector3f(0.4f), 20f, 25f);
    private final Matrix4f[] transformationMatrices = new Matrix4f[25];

    public TextureTestingLayer(String name)
    {
        super(name);
        for (int i = 0; i < 10; ++i)
        {
            renderer.getSceneLights().addLight(new PointLight
            (
                new Vector3f(i, Math.sin(i) + 5f, 0f),
                new Vector3f(Math.random(), Math.random(), 1f - Math.random()), 1.0f, 0.7f, 1.8f)
            );
        }
        renderer.getSceneLights().addLight(flashLight);
        for (int i = 0; i < 25; ++i)
        {
            transformationMatrices[i] = Matrix4f.translate(new Vector3f(i % 5 * 2.5f, EntryPoint.fastFloor(i / 5f) * 2.5f, -1f));
        }
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
            if (((MousePressedEvent) event).button == Button.MOUSE_BUTTON_1.buttonCode)
            {
                flashLight.setColor(new Vector3f(0f));
            }
            if (((MousePressedEvent) event).button == Button.MOUSE_BUTTON_2.buttonCode)
            {
                flashLight.setColor(new Vector3f(0.4f));
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
        for (int i = 0; i < transformationMatrices.length; ++i)
        {
            transformationMatrices[i] = Matrix4f.translate(new Vector3f(i % 5 * 2.5f, EntryPoint.fastFloor(i / 5f) * 2.5f, -1f)).multiply(Matrix4f.rotate(rotationAxis, rotation));
        }
    }

    @Override
    public void onRender()
    {
        renderer.begin(perspectiveCamera);
        for (int i = 0; i < 25; ++i)
        {
            if (i % 2 == 0) model.renderTemp(renderer, transformationMatrices[i]);
            else model2.renderTemp(renderer, transformationMatrices[i]);
        }
        wall.render(renderer);
    }

    @Override
    public void onDispose()
    {
        model.delete();
    }
}