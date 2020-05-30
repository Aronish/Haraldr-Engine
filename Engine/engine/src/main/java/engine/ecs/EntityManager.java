package engine.ecs;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EntityManager
{
    private List<Long> entities = new ArrayList<>();

    public void addEntity(@NotNull Entity entity)
    {
        entities.add(entity.getId());
    }
}
