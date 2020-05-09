package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.graphics.CubeMap;
import engine.graphics.CubeMapOld;
import engine.graphics.Model;
import engine.graphics.Renderer3D;
import engine.graphics.lighting.DirectionalLight;
import engine.graphics.lighting.SceneLights;
import engine.graphics.material.DiffuseMaterial;
import engine.input.Input;
import engine.input.Key;
import engine.layer.Layer;
import engine.main.Window;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class CubeMapLayer extends Layer
{
    private CubeMapOld environmentMap = new CubeMapOld("default_hdris/wooden_lounge_4k.hdr");

    private Model model = new Model(
            "models/suzanne_smooth.obj",
            new DiffuseMaterial("default_textures/MetalSpottyDiscoloration001_COL_4K_SPECULAR.jpg"),
            Matrix4f.scale(new Vector3f(1f))
    );

    public CubeMapLayer(String name)
    {
        super(name);
        SceneLights sceneLights = new SceneLights();
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(0f), new Vector3f(-1f, -2f, -3f), new Vector3f(1f, 1f, 0.8f));
        sceneLights.addLight(directionalLight);
        Renderer3D.setSceneLights(sceneLights);
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
    }

    @Override
    public void onUpdate(@NotNull Window window, float deltaTime)
    {
        if (Input.isKeyPressed(window, Key.KEY_UP))     Renderer3D.addExposure(1f * deltaTime);
        if (Input.isKeyPressed(window, Key.KEY_DOWN))   Renderer3D.addExposure(-1f * deltaTime);
    }

    @Override
    public void onRender()
    {
        model.render();
        environmentMap.renderSkyBox();
    }

    @Override
    public void onDispose()
    {
        environmentMap.delete();
    }
}
