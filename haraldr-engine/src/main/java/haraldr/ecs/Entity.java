package haraldr.ecs;

public class Entity
{
    public static final Entity INVALID = new Entity(-1);

    public final int id;

    public Entity(int id)
    {
        this.id = id;
    }
}
