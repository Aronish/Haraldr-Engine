package haraldr.ecs;

import haraldr.math.Vector3f;

public class TransformComponent
{
    public Vector3f position;
    public Vector3f scale;

    public TransformComponent(Vector3f position, Vector3f scale)
    {
        this.position = position;
        this.scale = scale;
    }
}
