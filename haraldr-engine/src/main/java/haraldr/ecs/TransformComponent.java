package haraldr.ecs;

import haraldr.math.Vector3f;
import org.jetbrains.annotations.Contract;

public class TransformComponent
{
    public Vector3f position;
    public Vector3f scale;

    @Contract(pure = true)
    public TransformComponent(Vector3f position, Vector3f scale)
    {
        this.position = position;
        this.scale = scale;
    }
}
