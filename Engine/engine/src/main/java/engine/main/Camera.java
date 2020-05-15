package engine.main;

import engine.event.MouseMovedEvent;
import engine.event.MouseScrolledEvent;
import engine.event.WindowFocusEvent;
import engine.math.Matrix4f;
import engine.math.Vector3f;

public abstract class Camera
{
    protected Vector3f position = Vector3f.IDENTITY;
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

    public void setPosition(Vector3f position)
    {
        this.position = position;
        calculateViewMatrix();
    }

    public void addPosition(Vector3f position)
    {
        this.position.add(position);
        calculateViewMatrix();
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public Matrix4f getViewMatrix()
    {
        return viewMatrix;
    }
}
