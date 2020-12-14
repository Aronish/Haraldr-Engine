package haraldr.ecs;

import org.jetbrains.annotations.Contract;

public class BoundingSphereComponent
{
    @SerializeField
    public float radius;

    @Contract(pure = true)
    public BoundingSphereComponent(float radius)
    {
        this.radius = radius;
    }
}
