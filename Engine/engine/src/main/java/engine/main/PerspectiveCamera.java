package engine.main;

import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "WeakerAccess"})
public class PerspectiveCamera
{
    private Matrix4f viewMatrix;
    private Matrix4f lookAt;
    private Vector3f position;

    private float pitch = 0f, yaw = 0f;
    private Vector3f direction = new Vector3f(
            (float) Math.cos(Math.toRadians(yaw) * Math.cos(Math.toRadians(pitch))),
            (float) Math.sin(Math.toRadians(pitch)),
            (float) Math.sin(Math.toRadians(yaw) * Math.cos(Math.toRadians(pitch)))
    );

    private PerspectiveCameraController controller;

    public PerspectiveCamera()
    {
        this(new Vector3f());
    }

    public PerspectiveCamera(Vector3f position)
    {
        this.position = position;
        this.controller = new PerspectiveCameraController(this);
        calculateViewMatrix();
    }

    public PerspectiveCamera(PerspectiveCameraController controller)
    {
        this(new Vector3f(), controller);
    }

    public PerspectiveCamera(Vector3f position, PerspectiveCameraController controller)
    {
        this.position = position;
        this.controller = controller;
        calculateViewMatrix();
    }

    public void calculateViewMatrix()
    {
        viewMatrix = lookAt(position, direction);
    }

    public Matrix4f lookAt(Vector3f position, Vector3f target)
    {
        direction = new Vector3f(
                (float) Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
                (float) Math.sin(Math.toRadians(pitch)),
                (float) Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))
        );
        direction.normalize();
        return Matrix4f.lookAt(position, Vector3f.add(position, direction), Vector3f.UP);
    }

    public void setPosition(@NotNull Vector3f position)
    {
        this.position = position;
        calculateViewMatrix();
    }

    public void addPosition(Vector3f position)
    {
        this.position.add(position);
        calculateViewMatrix();
    }

    public void setDirection(Vector3f direction)
    {
        this.direction = direction;
    }

    public void addYaw(float yaw)
    {
        this.yaw += yaw;
        calculateViewMatrix();
    }

    public void addPitch(float pitch)
    {
        this.pitch += pitch;
        if (this.pitch > 89f) this.pitch = 89f;
        if (this.pitch < -89f) this.pitch = -89f;
        calculateViewMatrix();
    }

    public Matrix4f getViewMatrix()
    {
        return viewMatrix;
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public Vector3f getDirection()
    {
        return this.direction;
    }

    public PerspectiveCameraController getController()
    {
        return controller;
    }
}
