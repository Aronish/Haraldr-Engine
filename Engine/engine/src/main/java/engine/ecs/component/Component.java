package engine.ecs.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Component
{
    //Potentially useless due to complicated code
    static <T extends Component> @Nullable T getComponent(Class<T> componentType, @NotNull List<Component> list)
    {
        for (Component component : list)
        {
            if (componentType.isInstance(component)) return componentType.cast(component);
        }
        return null;
    }
}
