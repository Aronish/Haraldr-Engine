package haraldr.ecs;

import org.jetbrains.annotations.Contract;

public class Entity
{
    public static final Entity INVALID = new Entity(-1);

    public final int id;

    @Contract(pure = true)
    public Entity(int id)
    {
        this.id = id;
    }
}
