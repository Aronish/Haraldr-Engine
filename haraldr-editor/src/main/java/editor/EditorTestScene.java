package editor;

import haraldr.ecs.BoundingSphereComponent;
import haraldr.ecs.Entity;
import haraldr.ecs.ModelComponent;
import haraldr.event.Event;
import haraldr.graphics.CubeMap;
import haraldr.graphics.Renderer3D;
import haraldr.graphics.lighting.PointLight;
import haraldr.input.Input;
import haraldr.input.KeyboardKey;
import haraldr.main.Window;
import haraldr.math.Vector3f;
import haraldr.scene.Scene3D;

public class EditorTestScene extends Scene3D
{
    private Entity ape = registry.createEntity(new Vector3f(0f, 0f, 0f), new Vector3f(0.5f));
    private float interpolation;

    private PointLight pointLight = new PointLight(new Vector3f(), new Vector3f(5f, 3f, 1f));

    @Override
    public void onClientActivate()
    {
        setSceneLights(
                pointLight
        );
        setSkyBox(CubeMap.createEnvironmentMap("default_hdris/NorwayForest_4K_hdri_sphere.hdr"));
        registry.addComponent(new ModelComponent("default_models/model.json"), ape);
        registry.addComponent(new BoundingSphereComponent(0.75f), ape);
    }

    @Override
    protected void onClientEvent(Event event, Window window)
    {
        if (Input.wasKeyPressed(event, KeyboardKey.KEY_R))
        {
            ModelComponent modelComponent = registry.getComponent(ModelComponent.class, ape);
            modelComponent.model.refresh();
        }
    }

    @Override
    protected void onClientUpdate(float deltaTime, Window window)
    {
        if (Input.isKeyPressed(window, KeyboardKey.KEY_UP))     Renderer3D.addExposure(deltaTime);
        if (Input.isKeyPressed(window, KeyboardKey.KEY_DOWN))   Renderer3D.addExposure(-deltaTime);
        if (Input.isKeyPressed(window, KeyboardKey.KEY_9))   interpolation += 1f * deltaTime;
        if (Input.isKeyPressed(window, KeyboardKey.KEY_7))   interpolation -= 1f * deltaTime;
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
