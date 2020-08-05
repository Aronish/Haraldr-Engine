package haraldr.scenegraph;

import haraldr.ecs.EntityRegistry;
import haraldr.ecs.ModelComponent;
import haraldr.ecs.TransformComponent;
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

    protected abstract void onClientActivate();
    protected abstract void onClientEvent(Event event, Window window);
    protected abstract void onClientUpdate(float deltaTime, Window window);
    protected abstract void onClientRender();
    protected abstract void onClientDispose();

    public final void onActivate()
    {
        onClientActivate();
    }

    public final void onEvent(Event event, Window window)
    {
        onClientEvent(event, window);
    }

    public final void onUpdate(Window window, float deltaTime)
    {
        onClientUpdate(deltaTime, window);
    }

    public final void onRender()
    {
        onClientRender();
        sceneLights.bind();
        registry.group(ModelComponent.class, TransformComponent.class).forEach((model, transform) -> model.model.render(transform));
        skyBox.renderSkyBox();
    }

    public final void onDispose()
    {
        onClientDispose();
        sceneLights.dispose();
    }
}