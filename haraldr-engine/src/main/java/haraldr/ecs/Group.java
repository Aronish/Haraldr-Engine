package haraldr.ecs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

//TODO: Maybe attempt making variable size groups if needed.
public class Group<A, B>
{
    private List<A> first = new ArrayList<>();
    private List<B> second = new ArrayList<>();
    private int size;

    public void add(A first, B second)
    {
        this.first.add(first);
        this.second.add(second);
        ++size;
    }

    public void forEach(BiConsumer<A, B> action)
    {
        for (int i = 0; i < size; ++i)
        {
            action.accept(first.get(i), second.get(i));
        }
    }
}
