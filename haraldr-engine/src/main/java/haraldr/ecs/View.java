package haraldr.ecs;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class View<T> implements Iterable<T>
{
    private List<T> components;

    public View(List<T> components)
    {
        this.components = components;
    }

    @NotNull
    @Override
    public Iterator<T> iterator()
    {
        return components.iterator();
    }
}
