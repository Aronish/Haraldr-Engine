package engine.system;

import engine.component.MeshComponent;
import engine.component.TransformComponent;
import engine.graphics.Renderer3D;

import java.util.HashMap;
import java.util.Map;

public class RenderSystem
{
    private Map<Long, MeshComponent> activeMeshComponents = new HashMap<>();
    private Map<Long, TransformComponent> activeTransformComponents = new HashMap<>();

    public void render()
    {
        for (long id : activeMeshComponents.keySet())
        {
            MeshComponent meshComponent = activeMeshComponents.get(id);
            meshComponent.bind(Renderer3D.getCamera().getPosition());
            if (activeTransformComponents.containsKey(id))
            {
                meshComponent.getMaterial().getShader().setMatrix4f(activeTransformComponents.get(id).getTransformation(), "model");
            }
            meshComponent.render();
        }
    }

    public void addMeshComponent(MeshComponent meshComponent, long id)
    {
        activeMeshComponents.put(id, meshComponent);
    }

    public void addTransformComponent(TransformComponent transformComponent, long id)
    {
        activeTransformComponents.put(id, transformComponent);
    }

    public void inactivateMeshComponent(long id)
    {
        activeMeshComponents.remove(id);
    }

    public void inactivateTransformComponent(long id)
    {
        activeTransformComponents.remove(id);
    }
}
