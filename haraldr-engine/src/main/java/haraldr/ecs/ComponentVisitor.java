package haraldr.ecs;

import haraldr.debug.Logger;

//Not that scalable outside engine, should probably change if we want more modularity
public interface ComponentVisitor
{
    default void visit(Component component)
    {
        Logger.warn("ComponentVisitor does not support component of type " + component.getClass().getSimpleName());
    }

    void visit(BoundingSphereComponent boundingSphereComponent);
    void visit(ModelComponent modelComponent);
    void visit(TagComponent tagComponent);
    void visit(TransformComponent transformComponent);
}