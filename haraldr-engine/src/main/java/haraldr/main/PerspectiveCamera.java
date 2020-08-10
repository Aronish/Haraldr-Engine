package haraldr.main;

import haraldr.event.MouseMovedEvent;
import haraldr.event.MouseScrolledEvent;
import haraldr.event.WindowFocusEvent;
import haraldr.event.WindowResizedEvent;
import haraldr.math.Matrix4f;
import haraldr.math.Vector3f;

@SuppressWarnings({"unused", "WeakerAccess"})
public class PerspectiveCamera extends Camera
{
    private float pitch = 0f, yaw = 0f;
    private Vector3f direction = new Vector3f(
            (float) Math.cos(Math.toRadians(yaw) * Math.cos(Math.toRadians(pitch))),
            (float) Math.sin(Math.toRadians(pitch)),
            (float) Math.sin(Math.toRadians(yaw) * Math.cos(Math.toRadians(pitch)))
    );
    private Vector3f right = Vector3f.normalize(Vector3f.cross(direction, Vector3f.UP));
    private Vector3f up = Vector3f.normalize(Vector3f.cross(right, direction));
    private Matrix4f lookAt;

    private float fov = 60f, near = 0.1f, far = 100f;

    private PerspectiveCameraController controller;

    public PerspectiveCamera()
    {
        this(Vector3f.IDENTITY);
    }

    public PerspectiveCamera(Vector3f position)
    {
        this.position = position;
        controller = new PerspectiveCameraController(this);
    }

    @Override
    public void handleMovement(Window window, float deltaTime)
    {
        controller.handleMovement(window, deltaTime);
    }

    @Override
    public void handleRotation(MouseMovedEvent event)
    {
        controller.handleRotation(event);
    }

    @Override
    public void handleScroll(MouseScrolledEvent event)
    {
        controller.handleScroll(event);
    }

    @Override
    public void onFocus(WindowFocusEvent event)
    {
        controller.onFocus(event);
    }

    @Override
    public void onResize(WindowResizedEvent event)
    {

    }

    @Override
    public void calculateViewMatrix()
    {
        direction = new Vector3f(
                (float) Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
                (float) Math.sin(Math.toRadians(pitch)),
                (float) Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))
        ).normalize();
        right = Vector3f.cross(direction, Vector3f.UP).normalize();
        up = Vector3f.cross(right, direction).normalize();
        viewMatrix = Matrix4f.lookAt(position, Vector3f.add(position, direction), Vector3f.UP);
    }

    @Override
    public void calculateProjectionMatrix()
    {

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

    public void rotate(float yaw, float pitch)
    {
        this.yaw += yaw;
        this.pitch += pitch;
        if (this.pitch > 89f) this.pitch = 89f;
        if (this.pitch < -89f) this.pitch = -89f;
        calculateViewMatrix();
    }

    public Vector3f getDirection()
    {
        return this.direction;
    }

    public Vector3f getRight()
    {
        return right;
    }

    public Vector3f getUp()
    {
        return up;
    }
}
