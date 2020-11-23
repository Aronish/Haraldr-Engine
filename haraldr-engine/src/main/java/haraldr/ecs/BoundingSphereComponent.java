package haraldr.ecs;

import org.jetbrains.annotations.Contract;

public class BoundingSphereComponent
{
    public float radius;

    @Contract(pure = true)
    public BoundingSphereComponent(float radius)
    {
        this.radius = radius;
    }
}
