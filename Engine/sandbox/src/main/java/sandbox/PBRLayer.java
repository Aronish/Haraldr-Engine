package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.KeyPressedEvent;
import engine.graphics.CubeMap;
import engine.graphics.Model;
import engine.graphics.Renderer3D;
import engine.graphics.lighting.PointLight;
import engine.graphics.lighting.SceneLights;
import engine.graphics.material.PBRMaterial;
import engine.input.Input;
import engine.input.Key;
import engine.layer.Layer;
import engine.main.Application;
import engine.main.Window;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class PBRLayer extends Layer
{
    private CubeMap environmentMap = CubeMap.createEnvironmentMap("default_hdris/TexturesCom_NorwayForest_4K_hdri_sphere.hdr");

    private PointLight l1 = new PointLight(new Vector3f(0f, 1f, 0f), new Vector3f(15f, 15f, 10f));

    private PBRMaterial material = new PBRMaterial(
            "default_textures/Cerberus_A.png",
            "default_textures/Cerberus_N.png",
            "default_textures/Cerberus_M.png",
            "default_textures/Cerberus_R.png",
            environmentMap
    );

    private Model model = new Model(
            "models/cerberus.obj",
            material,
            Matrix4f.rotate(Vector3f.UP, 180f)
    );

    public PBRLayer(String name)
    {
        super(name);
        SceneLights sl = new SceneLights();
        sl.addLight(l1);
        Renderer3D.setSceneLights(sl);
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

    private float sin, cos, rotation;

    @Override
    public void onUpdate(Window window, float deltaTime)
    {
        if (Input.isKeyPressed(window, Key.KEY_UP))     Renderer3D.addExposure(1f * deltaTime);
        if (Input.isKeyPressed(window, Key.KEY_DOWN))   Renderer3D.addExposure(-1f * deltaTime);
        sin = (float) Math.sin(Application.time / 3f);
        cos = (float) Math.cos(Application.time / 3f);
        //rotation += 10f * deltaTime;
        l1.setPosition(new Vector3f(sin, 0.3f, cos * 1.5f));
    }

    @Override
    public void onRender()
    {
        model.render();
        l1.render();
        environmentMap.renderSkyBox();
    }

    @Override
    public void onDispose()
    {
    }
}
