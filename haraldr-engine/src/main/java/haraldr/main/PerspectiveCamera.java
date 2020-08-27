package haraldr.main;

import haraldr.debug.Logger;
import haraldr.event.Event;
import haraldr.event.EventType;
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

    private float fov = 60f, aspectRatio, near = 0.1f, far = 100f;

    private PerspectiveCameraController cameraController;

    public PerspectiveCamera(float width, float height, PerspectiveCameraController cameraController)
    {
        this(width, height, Vector3f.IDENTITY, cameraController);
    }

    public PerspectiveCamera(float width, float height, Vector3f position, PerspectiveCameraController cameraController)
    {
        this.position = position;
        aspectRatio = width / height;
        this.cameraController = cameraController;
        this.cameraController.setReference(this);
        calculateViewMatrix();
        calculateProjectionMatrix();
    }

    @Override
    public void onUpdate(float deltaTime, Window window)
    {
        cameraController.onUpdate(deltaTime, window);
    }

    @Override
    public void onEvent(Event event, Window window)
    {
        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            var windowResizedEvent = (WindowResizedEvent) event;
            aspectRatio = (float) windowResizedEvent.width / (float) windowResizedEvent.height;
            calculateProjectionMatrix();
        }
        cameraController.onEvent(event, window);
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
        projectionMatrix = Matrix4f.perspective(fov, aspectRatio, near, far);
    }

    public void addFov(float fov)
    {
        this.fov += fov;
        if (this.fov < 10f) this.fov = 10f;
        if (this.fov > 179f) this.fov = 179f;
        calculateProjectionMatrix();
    }

    public void setYaw(float yaw)
    {
        this.yaw = yaw;
        calculateViewMatrix();
    }

    public void setPitch(float pitch)
    {
        this.pitch = pitch;
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
        return direction;
    }

    public Vector3f getRight()
    {
        return right;
    }

    public Vector3f getUp()
    {
        return up;
    }

    public float getYaw()
    {
        return yaw;
    }
}
