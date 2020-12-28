package haraldr.ecs;

import org.jetbrains.annotations.Contract;

public class BoundingSphereComponent implements Component
{
    public float radius;

    @Contract(pure = true)
    public BoundingSphereComponent(float radius)
    {
        this.radius = radius;
    }

    @Override
    public void acceptVisitor(ComponentVisitor visitor)
    {
        visitor.visit(this);
    }
}