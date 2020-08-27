package haraldr.scenegraph;

import haraldr.ecs.BoundingSphereComponent;
import haraldr.ecs.Entity;
import haraldr.ecs.EntityRegistry;
import haraldr.ecs.ModelComponent;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.CubeMap;
import haraldr.graphics.Renderer3D;
import haraldr.graphics.lighting.Light;
import haraldr.graphics.lighting.SceneLights;
import haraldr.main.Window;
import haraldr.math.Matrix4f;
import haraldr.math.Vector3f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics3D;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class Scene3D
{
    private CubeMap skyBox;
    private Entity selected = Entity.INVALID;

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

        //if (event.eventType == EventType.MOUSE_PRESSED)
        //{
        //    var mousePressedEvent = (MousePressedEvent) event;
        //    selected = selectEntity(mousePressedEvent.xPos, mousePressedEvent.yPos, window.getWidth(), window.getHeight(), selected, registry);
        //}
    }

    public final void onUpdate(float deltaTime, Window window)
    {
        onClientUpdate(deltaTime, window);
    }

    public final void onRender()
    {
        sceneLights.bind();
        skyBox.renderSkyBox();
        var renderables = registry.view(ModelComponent.class);
        renderables.forEach((transform, model) -> model.model.render(transform));
        onClientRender();
    }

    public final void onDispose()
    {
        onClientDispose();
        sceneLights.dispose();
    }
/*
    public static Entity selectEntity(int mouseX, int mouseY, int width, int height, Entity lastSelected, EntityRegistry registry)
    {
        Vector4f rayClipSpace = new Vector4f(
                (2f * mouseX) / width - 1f,
                1f - (2f * mouseY) / height,
                -1f,
                1f
        );
        Vector4f rayEyeSpace = Matrix4f.multiply(Matrix4f.invert(Matrix4f.perspective), rayClipSpace);
        rayEyeSpace.setZ(-1f);
        rayEyeSpace.setW(0f);

        Vector3f rayWorldSpace = new Vector3f(Matrix4f.multiply(Matrix4f.invert(Renderer3D.getCamera().getViewMatrix()), rayEyeSpace));
        rayWorldSpace.normalize();

        Entity selected;
        if (!lastSelected.equals(Entity.INVALID))
        {
            ModelComponent lastModel = registry.getComponent(ModelComponent.class, lastSelected);
            lastModel.model.setOutlined(false);
        }

        selected = registry.view(BoundingSphereComponent.class).find(((transform, bsphere) ->
                Physics3D.rayIntersectsSphere(Renderer3D.getCamera().getPosition(), rayWorldSpace, transform.position, bsphere.radius)), registry);

        if (!selected.equals(Entity.INVALID))
        {
            ModelComponent model = registry.getComponent(ModelComponent.class, selected);
            model.model.setOutlined(true);
        }
        return selected;
    }

 */
}