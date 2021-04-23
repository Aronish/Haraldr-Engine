package haraldr.scene;

import haraldr.ecs.EntityRegistry;
import haraldr.ecs.ModelComponent;
import haraldr.event.Event;
import haraldr.graphics.CubeMap;
import haraldr.graphics.lighting.Light;
import haraldr.graphics.lighting.SceneLights;
import haraldr.main.Window;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class Scene3D
{
    protected CubeMap skyBox;
    protected SceneLights sceneLights = new SceneLights();
    protected EntityRegistry entityRegistry = new EntityRegistry();

    public EntityRegistry getEntityRegistry()
    {
        return entityRegistry;
    }

    public void render()
    {
        sceneLights.bind();
        skyBox.renderSkyBox();
        var renderables = entityRegistry.view(ModelComponent.class);
        renderables.forEach((transform, model) -> model.model.render(transform));
        sceneLights.renderLights();
    }

    public void dispose()
    {
        sceneLights.dispose();
    }
}