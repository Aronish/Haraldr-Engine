package engine.main;

import engine.math.Matrix4f;
import engine.math.Vector2f;
import engine.math.Vector3f;

import org.jetbrains.annotations.NotNull;

public class OrthograhpicCamera
{
    private Matrix4f viewMatrix;

    protected Vector3f position;
    protected float scale = 1f;
    protected Vector2f scaleVector = new Vector2f(scale);

    public OrthograhpicCamera()
    {
        this(new Vector3f());
    }

    public OrthograhpicCamera(Vector3f position)
    {
        this.position = position;
        calculateViewMatrix();
    }

    protected void calculateViewMatrix()
    {
        viewMatrix = Matrix4f.transform(position, 0.0f, scaleVector.setBoth(scale), true);
    }

    public void setPosition(@NotNull Vector3f position)
    {
        this.position = position.multiply(scale);
        calculateViewMatrix();
    }

    public void addPosition(Vector3f pos)
    {
        position.add(pos);
        calculateViewMatrix();
    }

    public Matrix4f getViewMatrix()
    {
        return viewMatrix;
    }

    public float getScale()
    {
        return scale;
    }
}
