package haraldr.ecs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentStorage<T>
{
    private Map<Integer, T> components = new HashMap<>();

    public void add(T component, Entity entity)
    {
        components.putIfAbsent(entity.id, component);
    }

    public void remove(Entity entity)
    {
        components.remove(entity.id);
    }

    public T get(int entity)
    {
        return components.get(entity);
    }

    public int getEntityKey(T component)
    {
        for (Map.Entry<Integer, T> entry : components.entrySet())
        {
            if (component.equals(entry.getValue()))
            {
                return entry.getKey();
            }
        }
        return -1;
    }

    public List<Integer> getEntities()
    {
        return new ArrayList<>(components.keySet());
    }

    public List<T> getComponents()
    {
        return new ArrayList<>(components.values());
    }
}
