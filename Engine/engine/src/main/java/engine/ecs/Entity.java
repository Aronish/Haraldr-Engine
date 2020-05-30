package engine.ecs;

import engine.ecs.component.Component;

public class Entity
{
    private static long entityCount;

    private long id;

    public Entity()
    {
        id = entityCount++;
    }

    public void addComponent(Component component)
    {
        
    }

    public long getId()
    {
        return id;
    }
}
