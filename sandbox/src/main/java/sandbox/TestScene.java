package sandbox;

import haraldr.ecs.BoundingSphereComponent;
import haraldr.ecs.Entity;
import haraldr.ecs.ModelComponent;
import haraldr.event.Event;
import haraldr.graphics.CubeMap;
import haraldr.graphics.Renderer3D;
import haraldr.graphics.lighting.PointLight;
import haraldr.input.Input;
import haraldr.input.Key;
import haraldr.main.Window;
import haraldr.math.Vector3f;
import haraldr.scene.Scene3D;

public class TestScene extends Scene3D
{
    private Entity ape = registry.createEntity(new Vector3f(0f, 0f, 0f), new Vector3f(0.5f));
    private float interpolation;

    private PointLight pointLight = new PointLight(new Vector3f(), new Vector3f(2.5f, 2.5f, 7.5f));
    private PointLight pointLight2 = new PointLight(new Vector3f(1.4f, 2.3f, 0f), new Vector3f(10f, 3f, 3f));

    @Override
    public void onClientActivate()
    {
        setSceneLights(
                pointLight,
                pointLight2
        );
        setSkyBox(CubeMap.createEnvironmentMap("default_hdris/NorwayForest_4K_hdri_sphere.hdr"));
        registry.addComponent(new ModelComponent("default_models/model.json"), ape);
        registry.addComponent(new BoundingSphereComponent(0.75f), ape);
    }

    @Override
    protected void onClientEvent(Event event, Window window)
    {
    }

    @Override
    protected void onClientUpdate(float deltaTime, Window window)
    {
        if (Input.isKeyPressed(window, Key.KEY_UP))     Renderer3D.addExposure(deltaTime);
        if (Input.isKeyPressed(window, Key.KEY_DOWN))   Renderer3D.addExposure(-deltaTime);
        if (Input.isKeyPressed(window, Key.KEY_KP_9))   interpolation += 1f * deltaTime;
        if (Input.isKeyPressed(window, Key.KEY_KP_7))   interpolation -= 1f * deltaTime;
        pointLight.setPosition(new Vector3f(Math.sin(interpolation), 0f, Math.cos(interpolation)));
    }

    @Override
    protected void onClientRender()
    {
        sceneLights.renderLights();
    }

    @Override
    protected void onClientDispose()
    {
    }
}
