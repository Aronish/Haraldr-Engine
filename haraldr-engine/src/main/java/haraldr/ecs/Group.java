package haraldr.ecs;

import java.util.ArrayList;
import java.util.List;

public class Group<A, B>
{
    private List<TransformComponent> transforms = new ArrayList<>();
    private List<A> first = new ArrayList<>();
    private List<B> second = new ArrayList<>();
    private int size;

    public void add(TransformComponent transform, A first, B second)
    {
        transforms.add(transform);
        this.first.add(first);
        this.second.add(second);
        ++size;
    }

    public void forEach(TriConsumer<TransformComponent, A, B> action)
    {
        for (int i = 0; i < size; ++i)
        {
            action.accept(transforms.get(i), first.get(i), second.get(i));
        }
    }

    public Entity find(TriFunction<TransformComponent, A, B, Boolean> action, EntityRegistry registry)
    {
        for (int i = 0; i < size; ++i)
        {
            boolean found = action.apply(transforms.get(i), first.get(i), second.get(i));
            if (found)
            {
                return registry.getEntityOf(first.get(i));
            }
        }
        return Entity.INVALID;
    }

    @FunctionalInterface
    public interface TriConsumer<A, B, C>
    {
        void accept(A a, B b, C c);
    }

    @FunctionalInterface
    public interface TriFunction<A, B, C, R>
    {
        R apply(A a, B b, C c);
    }
}
