package sandbox;

import haraldr.ecs.Entity;
import haraldr.ecs.ModelComponent;
import haraldr.ecs.TransformComponent;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.graphics.CubeMap;
import haraldr.graphics.Renderer3D;
import haraldr.graphics.lighting.PointLight;
import haraldr.input.Input;
import haraldr.input.Key;
import haraldr.main.Window;
import haraldr.math.Vector3f;
import haraldr.scenegraph.Scene3D;

public class TestScene extends Scene3D
{
    private Entity model2 = registry.createEntity();
    private float interpolation;

    private PointLight pointLight = new PointLight(new Vector3f(), new Vector3f(2.5f, 2.5f, 7.5f));
    private PointLight pointLight2 = new PointLight(new Vector3f(0.4f, 0.3f, 0f), new Vector3f(10f, 3f, 3f));

    @Override
    public void onClientActivate()
    {
        setSceneLights(
                pointLight,
                pointLight2
        );
        setSkyBox(CubeMap.createEnvironmentMap("default_hdris/NorwayForest_4K_hdri_sphere.hdr"));
        registry.addComponent(new ModelComponent("default_models/test2.json"), model2);
        registry.addComponent(new TransformComponent(new Vector3f(0.4f, 0f, 0f), new Vector3f(0.5f)), model2);
    }

    @Override
    protected void onClientEvent(Event event, Window window)
    {
    }

    @Override
    protected void onClientUpdate(float deltaTime, Window window)
    {
        if (Input.isKeyPressed(window, Key.KEY_UP))     Renderer3D.addExposure(1f * deltaTime);
        if (Input.isKeyPressed(window, Key.KEY_DOWN))   Renderer3D.addExposure(-1f * deltaTime);
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
