package haraldr.scene;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MouseScrolledEvent;
import haraldr.event.WindowResizedEvent;
import haraldr.input.Input;
import haraldr.main.Window;
import haraldr.math.Matrix4f;
import haraldr.math.Vector3f;

import static haraldr.input.Key.KEY_A;
import static haraldr.input.Key.KEY_C;
import static haraldr.input.Key.KEY_D;
import static haraldr.input.Key.KEY_S;
import static haraldr.input.Key.KEY_SPACE;
import static haraldr.input.Key.KEY_W;

@SuppressWarnings("unused")
public class FPSCamera extends Camera
{
    private static final float CAMERA_SPEED = 0.75f;
    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float ZOOM_SENSITIVITY = 2f;

    private float pitch = 0f, yaw = 0f;
    private Vector3f direction = new Vector3f(
            (float) Math.cos(Math.toRadians(yaw) * Math.cos(Math.toRadians(pitch))),
            (float) Math.sin(Math.toRadians(pitch)),
            (float) Math.sin(Math.toRadians(yaw) * Math.cos(Math.toRadians(pitch)))
    );
    private Vector3f right = Vector3f.normalize(Vector3f.cross(direction, Vector3f.UP));
    private Vector3f up = Vector3f.normalize(Vector3f.cross(right, direction));

    private float fov = 60f, aspectRatio, near = 0.1f, far = 100f, zoom = 1f;
    private float lastX, lastY;

    public FPSCamera(float width, float height)
    {
        this(width, height, Vector3f.IDENTITY);
    }

    public FPSCamera(float width, float height, Vector3f position)
    {
        this.position = position;
        aspectRatio = width / height;
        calculateViewMatrix();
        calculateProjectionMatrix();
    }

    @Override
    public void onUpdate(float deltaTime, Window window)
    {
        if (Input.isKeyPressed(window, KEY_W))
        {
            addPosition(Vector3f.multiply(direction, CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_S))
        {
            addPosition(Vector3f.multiply(direction, -CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_D))
        {
            addPosition(Vector3f.multiply(right, CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_A))
        {
            addPosition(Vector3f.multiply(right, -CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_SPACE))
        {
            addPosition(Vector3f.multiply(up, CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_C))
        {
            addPosition(Vector3f.multiply(up, -CAMERA_SPEED * deltaTime));
        }
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
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            var mouseMovedEvent = (MouseMovedEvent) event;
            if (!window.isCursorVisible()) rotate(((float) mouseMovedEvent.xPos - lastX) * MOUSE_SENSITIVITY, (lastY - (float) mouseMovedEvent.yPos) * MOUSE_SENSITIVITY);
            lastX = (float) mouseMovedEvent.xPos;
            lastY = (float) mouseMovedEvent.yPos;
        }
        if (event.eventType == EventType.MOUSE_SCROLLED) addFov((float) -((MouseScrolledEvent) event).yOffset * ZOOM_SENSITIVITY);
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

    private void addZoom(float zoom)
    {
        this.zoom += zoom;
        if (this.zoom < 0.1f) this.zoom = 0.1f;
        calculateViewMatrix();
    }

    private void addFov(float fov)
    {
        this.fov += fov;
        if (this.fov < 10f) this.fov = 10f;
        if (this.fov > 179f) this.fov = 179f;
        calculateProjectionMatrix();
    }

    private void addYaw(float yaw)
    {
        this.yaw = yaw;
        calculateViewMatrix();
    }

    private void addPitch(float pitch)
    {
        this.pitch += pitch;
        if (this.pitch > 89f) this.pitch = 89f;
        if (this.pitch < -89f) this.pitch = -89f;
        calculateViewMatrix();
    }

    private void rotate(float yaw, float pitch)
    {
        this.yaw += yaw;
        this.pitch += pitch;
        if (this.pitch > 89f) this.pitch = 89f;
        if (this.pitch < -89f) this.pitch = -89f;
        calculateViewMatrix();
    }
}