package haraldr.scene;

import haraldr.event.Event;
import haraldr.main.Window;
import haraldr.math.Matrix4f;
import haraldr.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public abstract class Camera
{
    protected Vector3f position = Vector3f.IDENTITY;
    protected float[] rawPosition = new float[3];
    protected Matrix4f viewMatrix, projectionMatrix;
    protected float aspectRatio;

    public abstract void onUpdate(float deltaTime, Window window);

    public abstract void onEvent(Event event, Window window, boolean movable);

    public abstract void calculateViewMatrix();

    public abstract void calculateProjectionMatrix();

    public void setPosition(@NotNull Vector3f position)
    {
        this.position = position;
        rawPosition[0] = position.getX();
        rawPosition[1] = position.getY();
        rawPosition[2] = position.getZ();
        calculateViewMatrix();
    }

    public void addPosition(Vector3f position)
    {
        this.position.add(position);
        rawPosition[0] += position.getX();
        rawPosition[1] += position.getY();
        rawPosition[2] += position.getZ();
        calculateViewMatrix();
    }

    public void setAspectRatio(float aspectRatio)
    {
        this.aspectRatio = aspectRatio;
        calculateProjectionMatrix();
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public float[] getRawPosition()
    {
        return rawPosition;
    }

    public Matrix4f getViewMatrix()
    {
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix()
    {
        return projectionMatrix;
    }
}
