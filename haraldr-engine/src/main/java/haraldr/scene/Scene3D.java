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
    private CubeMap skyBox;

    protected SceneLights sceneLights;
    protected EntityRegistry registry = new EntityRegistry();

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

    public EntityRegistry getRegistry()
    {
        return registry;
    }

    protected abstract void onClientActivate();
    
    public final void onActivate()
    {
        onClientActivate();
    }

    protected abstract void onClientEvent(Event event, Window window);

    public final void onEvent(Event event, Window window)
    {
        onClientEvent(event, window);
    }

    protected abstract void onClientUpdate(float deltaTime, Window window);

    public final void onUpdate(float deltaTime, Window window)
    {
        onClientUpdate(deltaTime, window);
    }

    protected abstract void onClientRender();

    public final void onRender()
    {
        sceneLights.bind();
        skyBox.renderSkyBox();
        var renderables = registry.view(ModelComponent.class);
        renderables.forEach((transform, model) -> model.model.render(transform));
        onClientRender();
    }

    protected abstract void onClientDispose();

    public final void onDispose()
    {
        onClientDispose();
        sceneLights.dispose();
        skyBox.delete();
    }
}