package haraldr.ecs;

public interface Component
{
    void acceptVisitor(ComponentVisitor visitor);
}