package haraldr.graphics;

import haraldr.ecs.EntityRegistry;
import haraldr.ecs.ModelComponent;
import haraldr.graphics.lighting.Light;
import haraldr.graphics.lighting.SceneLights;

public class DynamicScene
{
    private CubeMap skyBox;

    private SceneLights sceneLights = new SceneLights();
    private EntityRegistry entityRegistry = new EntityRegistry();

    public DynamicScene()
    {
        skyBox = CubeMap.createEnvironmentMap("default_hdris/NorwayForest_4K_hdri_sphere.hdr");
    }

    public void setSkyBox(CubeMap skyBox)
    {
        this.skyBox = skyBox;
    }

    public void setSceneLights(SceneLights sceneLights)
    {
        this.sceneLights = sceneLights;
    }

    public void setSceneLights(Light... lights)
    {
        sceneLights = new SceneLights();
        for (Light light : lights)
        {
            sceneLights.addLight(light);
        }
    }

    public void setEntityRegistry(EntityRegistry entityRegistry)
    {
        this.entityRegistry = entityRegistry;
    }

    public void render()
    {
        sceneLights.bind();
        skyBox.renderSkyBox();
        var renderables = entityRegistry.view(ModelComponent.class);
        renderables.forEach((transform, model) -> model.model.render(transform));
    }

    public void dispose()
    {
        sceneLights.dispose();
        skyBox.delete();
    }

    public EntityRegistry getEntityRegistry()
    {
        return entityRegistry;
    }

    public SceneLights getSceneLights()
    {
        return sceneLights;
    }

    public String getEnvironmentMapPath()
    {
        return skyBox.getPath();
    }
}
