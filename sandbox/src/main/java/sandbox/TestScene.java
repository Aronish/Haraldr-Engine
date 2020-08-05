package sandbox;

import haraldr.ecs.Entity;
import haraldr.ecs.ModelComponent;
import haraldr.ecs.TransformComponent;
import haraldr.event.Event;
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
    private Entity model1 = registry.createEntity();
    private Entity model2 = registry.createEntity();

    @Override
    public void onClientActivate()
    {
        setSceneLights(
                new PointLight(new Vector3f(0f, 0.2f, 0f), new Vector3f(4.5f, 2.5f, 3f)),
                new PointLight(new Vector3f(), new Vector3f(2.5f, 2.5f, 7.5f))
        );
        setSkyBox(CubeMap.createEnvironmentMap("default_hdris/NorwayForest_4K_hdri_sphere.hdr"));
        registry.addComponent(new ModelComponent("default_models/test.json"), model1);
        registry.addComponent(new TransformComponent(new Vector3f(), new Vector3f(0.5f)), model1);
        registry.addComponent(new ModelComponent("default_models/test2.json"), model2);
        registry.addComponent(new TransformComponent(new Vector3f(2f, 0f, 0f), new Vector3f(0.5f)), model2);

        registry.destroyEntity(model1);

        model1 = registry.createEntity();
        Entity three = registry.createEntity();
    }

    @Override
    public void onClientEvent(Window window, Event event)
    {
    }

    @Override
    public void onClientUpdate(Window window, float deltaTime)
    {
        if (Input.isKeyPressed(window, Key.KEY_UP))     Renderer3D.addExposure(1f * deltaTime);
        if (Input.isKeyPressed(window, Key.KEY_DOWN))   Renderer3D.addExposure(-1f * deltaTime);
    }

    @Override
    protected void onClientRender()
    {
    }

    @Override
    protected void onClientDispose()
    {
    }
}
