package engine.main;

import engine.math.Matrix4f;
import engine.math.Vector2f;
import engine.math.Vector3f;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class OrthographicCamera
{
    private Matrix4f viewMatrix;

    protected Vector3f position;
    protected float scale = 1f;
    protected Vector2f scaleVector = new Vector2f(scale);

    public OrthographicCamera()
    {
        this(new Vector3f());
    }

    public OrthographicCamera(Vector3f position)
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

    public void setScale(float scale)
    {
        this.scale = scale;
        calculateViewMatrix();
    }

    public void setScale(float scaleX, float scaleY)
    {
        scaleVector.set(scaleX, scaleY);
        calculateViewMatrix();
    }

    public void addScale(float scale)
    {
        this.scale += scale;
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