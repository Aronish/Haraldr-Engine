package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.graphics.CubeMap;
import engine.graphics.ForwardRenderer;
import engine.graphics.Model;
import engine.graphics.lighting.DirectionalLight;
import engine.graphics.lighting.SceneLights;
import engine.graphics.lighting.Spotlight;
import engine.graphics.material.RefractiveMaterial;
import engine.input.Button;
import engine.input.Input;
import engine.input.Key;
import engine.layer.Layer;
import engine.main.Window;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class CubeMapLayer extends Layer
{
    private final Spotlight flashLight = new Spotlight(Vector3f.IDENTITY, Vector3f.IDENTITY, new Vector3f(1f), 10f, 25f);

    private CubeMap environmentMap = new CubeMap("default_hdris/cape_hill_4k.hdr");

    private Model refractiveSuzanne = new Model(
            "models/suzanne_semi_smooth.obj",
            new RefractiveMaterial(
                    environmentMap,
                    "default_textures/MetalSpottyDiscoloration001_COL_4K_SPECULAR.jpg",
                    "default_textures/MetalSpottyDiscoloration001_REFL_4K_SPECULAR.jpg"
            )
    );

    public CubeMapLayer(String name)
    {
        super(name);
        SceneLights sceneLights = new SceneLights();
        sceneLights.addLight(flashLight);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(0f), new Vector3f(-1f, -2f, -3f), new Vector3f(1f, 1f, 0.8f));
        sceneLights.addLight(directionalLight);
        ForwardRenderer.setSceneLights(sceneLights);
    }

    @Override
    public void onAttach(Window window)
    {
    }

    @Override
    public void onEvent(Window window, @NotNull Event event)
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

    @Override
    public void onUpdate(@NotNull Window window, float deltaTime)
    {
        if (Input.isKeyPressed(window, Key.KEY_UP))     ForwardRenderer.addExposure(1f * deltaTime);
        if (Input.isKeyPressed(window, Key.KEY_DOWN))   ForwardRenderer.addExposure(-1f * deltaTime);
        flashLight.setDirection(ForwardRenderer.getPerspectiveCamera().getDirection());
        flashLight.setPosition(ForwardRenderer.getPerspectiveCamera().getPosition());
    }

    @Override
    public void onRender()
    {
        environmentMap.renderSkyBox();
    }

    @Override
    public void onDispose()
    {
        environmentMap.delete();
    }
}
