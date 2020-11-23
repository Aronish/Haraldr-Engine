package haraldr.ecs;

import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class View<T>
{
    private List<TransformComponent> transforms;
    private List<T> components;

    @Contract(pure = true)
    public View(List<TransformComponent> transforms, List<T> components)
    {
        this.transforms = transforms;
        this.components = components;
    }

    public void forEach(BiConsumer<TransformComponent, T> action)
    {
        for (int i = 0; i < components.size(); ++i)
        {
            action.accept(transforms.get(i), components.get(i));
        }
    }

    public Entity find(BiFunction<TransformComponent, T, Boolean> action, EntityRegistry registry)
    {
        for (int i = 0; i < components.size(); ++i)
        {
            boolean found = action.apply(transforms.get(i), components.get(i));
            if (found)
            {
                return registry.getEntityOf(components.get(i));
            }
        }
        return Entity.INVALID;
    }
}
