package haraldr.ecs;

import haraldr.math.Quaternion;
import haraldr.math.Vector3f;
import org.jetbrains.annotations.Contract;

public class TransformComponent implements Component
{
    public Vector3f position;
    public Vector3f scale;
    public Vector3f rotation;
    public Quaternion rotationQuaternion;

    @Contract(pure = true)
    public TransformComponent(Vector3f position, Vector3f scale, Vector3f rotation)
    {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
        rotationQuaternion = Quaternion.fromEulerAngles(rotation);
    }

    @Override
    public void acceptVisitor(ComponentVisitor visitor)
    {
        visitor.visit(this);
    }
}