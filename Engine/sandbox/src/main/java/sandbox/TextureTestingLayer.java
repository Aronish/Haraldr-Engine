package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.graphics.DefaultModels;
import engine.graphics.Model;
import engine.graphics.Renderer3D;
import engine.graphics.lighting.PointLight;
import engine.graphics.lighting.SceneLights;
import engine.graphics.lighting.Spotlight;
import engine.graphics.material.NormalMaterial;
import engine.input.Button;
import engine.input.Input;
import engine.layer.Layer;
import engine.main.Application;
import engine.main.Window;
import engine.math.Matrix4f;
import engine.math.Vector2f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class TextureTestingLayer extends Layer
{
    private final Model model = new Model(
            "models/suzanne.obj",
            new NormalMaterial(
                    "default_textures/BricksPaintedWhite_COL_4K.jpg",
                    "default_textures/BricksPaintedWhite_NRM_4K.jpg"
            ),
            Matrix4f.translate(new Vector3f(0f, 4f, 0f)).multiply(Matrix4f.scale(new Vector3f(1f)))
    );

    private final Spotlight flashLight = new Spotlight(Vector3f.IDENTITY, Vector3f.IDENTITY, new Vector3f(1f), 20f, 25f);
    private final PointLight pointLight = new PointLight(new Vector3f(5f, 5f, -4f), new Vector3f(1f));

    public TextureTestingLayer(String name)
    {
        super(name);
        SceneLights sceneLights = new SceneLights();
        for (int i = 0; i < 10; ++i)
        {
            sceneLights.addLight(new PointLight(
                    new Vector3f(Math.cos(i * (Math.PI / 5f)) * 1.8f, 4f, Math.sin(i * (Math.PI / 5f)) * 1.8f),
                    new Vector3f(Math.random(), Math.random(), 1f - Math.random()), 1.0f, 0.7f, 1.8f)
            );
        }
        sceneLights.addLight(flashLight);
        sceneLights.addLight(pointLight);
        Renderer3D.setSceneLights(sceneLights);
    }

    @Override
    public void onAttach(Window window)
    {
    }

    @Override
    public void onEvent(@NotNull Window window, @NotNull Event event)
    {
        if (event.eventType == EventType.KEY_PRESSED)
        {
            EventHandler.onKeyPress((KeyPressedEvent) event, window);
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
        float sin = (float) Math.sin(Application.time / 2);
        float cos = (float) Math.cos(Application.time / 2);
        rotation += 10f * deltaTime;
        flashLight.setDirection(Renderer3D.getPerspectiveCamera().getDirection());
        flashLight.setPosition(Renderer3D.getPerspectiveCamera().getPosition());
        pointLight.setPosition(new Vector3f(cos * 2f + 2f, sin * 2f + 2f, -2f));
    }

    @Override
    public void onRender()
    {
        model.render();
        pointLight.render();
    }

    @Override
    public void onDispose()
    {
        //model.delete();
    }
}