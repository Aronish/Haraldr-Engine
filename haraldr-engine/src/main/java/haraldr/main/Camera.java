package haraldr.main;

import haraldr.event.MouseMovedEvent;
import haraldr.event.MouseScrolledEvent;
import haraldr.event.WindowFocusEvent;
import haraldr.math.Matrix4f;
import haraldr.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public abstract class Camera
{
    protected Vector3f position = Vector3f.IDENTITY;
    private float[] rawPosition = new float[3];
    protected Matrix4f viewMatrix;

    protected Camera()
    {
        calculateViewMatrix();
    }

    public abstract void handleMovement(Window window, float deltaTime);

    public abstract void handleRotation(MouseMovedEvent event);

    public abstract void handleScroll(MouseScrolledEvent event);

    public abstract void onFocus(WindowFocusEvent event);

    public abstract void calculateViewMatrix();

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
}
