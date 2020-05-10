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
import engine.main.Window;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class PBRLayer extends Layer
{
    private CubeMap environmentMap = CubeMap.createEnvironmentMap("default_hdris/wooden_lounge_4k.hdr");

    private PointLight l1 = new PointLight(new Vector3f(1.6f, 2f, 0.6f), new Vector3f(5f, 5f, 3f));
    private PointLight l2 = new PointLight(new Vector3f(1.0f, 2f, 0.6f), new Vector3f(5f, 5f, 3f));
    private PointLight l3 = new PointLight(new Vector3f(1.6f, 2f, 0.0f), new Vector3f(5f, 5f, 3f));
    private PointLight l4 = new PointLight(new Vector3f(1.0f, 2f, 0.0f), new Vector3f(5f, 5f, 3f));

    private Model model = new Model(
            "models/suzanne_smooth.obj",
            new PBRMaterial(
                    "default_textures/MetalSpottyDiscoloration001_COL_4K_METALNESS.jpg",
                    "default_textures/MetalSpottyDiscoloration001_NRM_4K_METALNESS.jpg",
                    "default_textures/MetalSpottyDiscoloration001_METALNESS_4K_METALNESS.jpg",
                    "default_textures/MetalSpottyDiscoloration001_ROUGHNESS_4K_METALNESS.jpg",
                    environmentMap
            ),
            Matrix4f.rotate(new Vector3f(1f, 0f, 0f), 90f)
    );

    public PBRLayer(String name)
    {
        super(name);
        SceneLights sl = new SceneLights();
        sl.addLight(l1);
        sl.addLight(l2);
        sl.addLight(l3);
        sl.addLight(l4);
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
        //sin = (float) Math.sin(Application.time / 3f);
        //cos = (float) Math.cos(Application.time / 3f);
        //rotation += 10f * deltaTime;
    }

    @Override
    public void onRender()
    {
        model.renderTransformed(Matrix4f.rotate(Vector3f.UP, rotation));
        l1.render();
        l2.render();
        l3.render();
        l4.render();
        environmentMap.renderSkyBox();
    }

    @Override
    public void onDispose()
    {
        environmentMap.delete();
    }
}
