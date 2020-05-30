package engine.ecs;

import engine.ecs.component.MeshComponent;
import engine.ecs.component.TransformComponent;

import java.util.HashMap;
import java.util.Map;

public class ComponentManager
{
    private Map<Long, MeshComponent> meshComponent = new HashMap<>();
    private Map<Long, TransformComponent> transformComponents = new HashMap<>();


}
