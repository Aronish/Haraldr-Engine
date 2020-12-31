package editor;

import haraldr.ecs.BoundingSphereComponent;
import haraldr.ecs.Entity;
import haraldr.ecs.ModelComponent;
import haraldr.ecs.TagComponent;
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
    private Entity ape = registry.createEntity(new Vector3f(0f, 0f, 0f), new Vector3f(0.5f), new Vector3f());
    private Entity ape2 = registry.createEntity(new Vector3f(0f, 4f, 0f), new Vector3f(0.5f), new Vector3f());

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
        registry.addComponent(new TagComponent("Suzanne"), ape);
        registry.addComponent(new ModelComponent("default_models/model.json"), ape2);
        registry.addComponent(new TagComponent("Ball"), ape2);
    }

    @Override
    protected void onClientEvent(Event event, Window window)
    {
    }

    @Override
    protected void onClientUpdate(float deltaTime, Window window)
    {
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
