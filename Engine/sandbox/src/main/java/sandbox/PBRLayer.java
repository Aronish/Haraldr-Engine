package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.graphics.CubeMap;
import engine.graphics.Model;
import engine.graphics.Renderer3D;
import engine.graphics.lighting.PointLight;
import engine.graphics.lighting.SceneLights;
import engine.graphics.material.Material;
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
    private CubeMap environmentMap = CubeMap.createEnvironmentMap("default_hdris/NorwayForest_4K_hdri_sphere.hdr");

    private PointLight l1 = new PointLight(new Vector3f(0f, 1f, 0f), new Vector3f(7.5f, 2.5f, 2.5f));
    private PointLight l2 = new PointLight(new Vector3f(0f, 1f, 0f), new Vector3f(5f, 1f, 1f));

    private Material pbr = new PBRMaterial(
            new Vector3f(0.8f, 0.2f, 0.3f),
            0f,
            0.4f,
            environmentMap
    );

    private Material pbr2 = new PBRMaterial(
            "default_textures/Cerberus_A.png",
            "default_textures/Cerberus_N.png",
            "default_textures/Cerberus_M.png",
            "default_textures/Cerberus_R.png",
            environmentMap
    );

    private Model model = new Model(
            "default_meshes/cerberus.obj",
            pbr2,
            Matrix4f.createRotate(Vector3f.UP, 180f).scale(new Vector3f(0.8f))
    );

    public PBRLayer(String name)
    {
        super(name);
        SceneLights sl = new SceneLights();
        sl.addLight(l1);
        sl.addLight(l2);
        Renderer3D.setSceneLights(sl);
    }

    @Override
    public void onEvent(@NotNull Window window, @NotNull Event event)
    {
        if (event.eventType == EventType.KEY_PRESSED)
        {
            if (Input.wasKey(event, Key.KEY_L)) renderLights = !renderLights;
        }
    }

    private float sin, cos, rotation, interpolation, interpolation2;
    private boolean renderLights = true;

    @Override
    public void onUpdate(Window window, float deltaTime)
    {
        if (Input.isKeyPressed(window, Key.KEY_UP))     Renderer3D.addExposure(1f * deltaTime);
        if (Input.isKeyPressed(window, Key.KEY_DOWN))   Renderer3D.addExposure(-1f * deltaTime);
        if (Input.isKeyPressed(window, Key.KEY_KP_7))   interpolation += 1f * deltaTime;
        if (Input.isKeyPressed(window, Key.KEY_KP_9))   interpolation -= 1f * deltaTime;
        if (Input.isKeyPressed(window, Key.KEY_KP_1))   interpolation2 += 1f * deltaTime;
        if (Input.isKeyPressed(window, Key.KEY_KP_3))   interpolation2 -= 1f * deltaTime;
        //sin = (float) Math.sin(Application.time / 3f);
        //cos = (float) Math.cos(Application.time / 3f);
        //rotation += 10f * deltaTime;
        l1.setPosition(new Vector3f(Math.sin(interpolation) * 0.3f, 0f, Math.cos(interpolation)));
        l2.setPosition(new Vector3f(Math.sin(interpolation2) * 0.3f, 0.3f, Math.cos(interpolation2)));
    }

    @Override
    public void onRender()
    {
        model.render();
        if (renderLights)
        {
            l1.render();
            l2.render();
        }
        environmentMap.renderSkyBox();
    }

    @Override
    public void onDispose()
    {
    }
}
