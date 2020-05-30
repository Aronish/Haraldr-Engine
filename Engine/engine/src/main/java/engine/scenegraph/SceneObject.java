package engine.scenegraph;

import engine.ecs.component.MeshComponent;
import engine.ecs.component.TransformComponent;
import engine.ecs.system.RenderSystem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SceneObject
{
    private static long sceneObjectCount;

    private long id;
    private SceneObject parent;
    private List<SceneObject> children = new ArrayList<>();

    private MeshComponent meshComponent;
    private TransformComponent transformComponent;

    public SceneObject(@NotNull Scene scene)
    {
        scene.addChildNode(this);
        id = sceneObjectCount++;
    }

    public SceneObject(@NotNull Scene scene, @NotNull SceneObject parent)
    {
        this.parent = parent;
        parent.addChildObject(this);
        scene.addChildNode(this);
        id = sceneObjectCount++;
    }

    public void addChildObject(SceneObject childNode)
    {
        children.add(childNode);
    }

    public void setMeshComponent(@NotNull RenderSystem renderSystem, MeshComponent meshComponent)
    {
        this.meshComponent = meshComponent;
        renderSystem.addMeshComponent(meshComponent, id);
    }

    public void setTransformComponent(@NotNull RenderSystem renderSystem, TransformComponent transformComponent)
    {
        this.transformComponent = transformComponent;
        this.transformComponent.setAttached(id);
        if (parent != null && parent.transformComponent != null)
        {
            transformComponent.setFromParent(parent.transformComponent);
        }
        renderSystem.addTransformComponent(this.transformComponent, id);
    }

    public void inactivate(@NotNull RenderSystem renderSystem)
    {
        renderSystem.inactivateMeshComponent(id);
        renderSystem.inactivateTransformComponent(id);
    }
}
