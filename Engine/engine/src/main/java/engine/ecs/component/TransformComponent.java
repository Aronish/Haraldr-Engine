package engine.ecs.component;

import engine.math.Matrix4f;
import engine.math.Quaternion;
import engine.math.Vector3f;
import engine.ecs.system.RenderSystem;
import org.jetbrains.annotations.NotNull;

public class TransformComponent implements Component
{
    private Vector3f position = Vector3f.IDENTITY;
    private Quaternion rotation = Quaternion.IDENTITY;
    private Vector3f scale = new Vector3f(1f);

    private Matrix4f transformation;

    private long attached;

    public TransformComponent()
    {
        calculateTransformationMatrix();
    }

    public TransformComponent(Vector3f position, Quaternion rotation, Vector3f scale)
    {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        calculateTransformationMatrix();
    }

    public void setAttached(long attached)
    {
        this.attached = attached;
    }

    public void setFromParent(@NotNull TransformComponent parentTransformation)
    {
        transformation.multiplyThis(parentTransformation.transformation);
    }

    public TransformComponent setPosition(Vector3f position)
    {
        this.position = position;
        calculateTransformationMatrix();
        return this;
    }

    public TransformComponent setRotation(Vector3f axis, float rotation)
    {
        this.rotation = Quaternion.fromAxis(axis, rotation);
        calculateTransformationMatrix();
        return this;
    }

    public TransformComponent setScale(Vector3f scale)
    {
        this.scale = scale;
        calculateTransformationMatrix();
        return this;
    }

    private void calculateTransformationMatrix()
    {
        transformation = Matrix4f.createTranslate(position).rotate(rotation).scale(scale);
    }

    public void updateRenderSystem(@NotNull RenderSystem renderSystem)
    {

    }

    public Matrix4f getTransformation()
    {
        return transformation;
    }
}
