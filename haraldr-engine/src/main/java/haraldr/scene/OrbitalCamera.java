package haraldr.scene;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MouseScrolledEvent;
import haraldr.event.WindowResizedEvent;
import haraldr.input.Button;
import haraldr.input.Input;
import haraldr.input.Key;
import haraldr.main.Window;
import haraldr.math.Matrix4f;
import haraldr.math.Vector3f;

public class OrbitalCamera extends Camera
{
    private static final float MOVE_SPEED = 4f;

    private Vector3f right = new Vector3f();
    private Vector3f up = new Vector3f();
    private Vector3f target = new Vector3f(1f);
    private float pitch, lastPitch, yaw, fov = 60f, aspectRatio, near = 0.1f, far = 100f, zoom = 1f, lastX, lastY;
    private boolean mouseHeld, shiftHeld;

    public OrbitalCamera(float width, float height)
    {
        this(width, height, Vector3f.IDENTITY);
    }

    public OrbitalCamera(float width, float height, Vector3f position)
    {
        this.position = position;
        aspectRatio = width / height;
        calculateViewMatrix();
        calculateProjectionMatrix();
    }

    @Override
    public void onUpdate(float deltaTime, Window window)
    {
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
        if (Input.wasMousePressed(event, Button.MOUSE_BUTTON_1)) mouseHeld = true;
        if (Input.wasMouseReleased(event, Button.MOUSE_BUTTON_1)) mouseHeld = false;
        if (Input.wasKeyPressed(event, Key.KEY_LEFT_SHIFT)) shiftHeld = true;
        if (Input.wasKeyReleased(event, Key.KEY_LEFT_SHIFT)) shiftHeld = false;
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            var mouseMovedEvent = (MouseMovedEvent) event;
            if (mouseHeld)
            {
                if (shiftHeld)
                {
                    target.add(Vector3f.add(
                            Vector3f.multiply(right, (float) (lastX - mouseMovedEvent.xPos) / window.getWidth()),
                            Vector3f.multiply(up, (float) (mouseMovedEvent.yPos - lastY) / window.getHeight())
                    ).multiply(MOVE_SPEED));
                    calculateViewMatrix();
                } else
                {
                    float yaw = (float) (mouseMovedEvent.xPos - lastX) / window.getWidth() * 360f;
                    float pitch = (float) (mouseMovedEvent.yPos) / window.getHeight() * 180f - 90f;
                    addYaw(yaw);
                    addPitch(pitch - lastPitch);
                }
            }
            lastPitch = (float)(mouseMovedEvent.yPos) / window.getHeight() * 180f - 90f;
            lastX = (float) mouseMovedEvent.xPos;
            lastY = (float) mouseMovedEvent.yPos;
            //TODO: Reduce calculations
        }
        if (event.eventType == EventType.MOUSE_SCROLLED)
        {
            addZoom((float) -((MouseScrolledEvent) event).yOffset);
        }
    }

    @Override
    public void calculateViewMatrix()
    {
        position = new Vector3f(
                Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
                Math.sin(Math.toRadians(pitch)),
                Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))
        ).multiply(zoom).add(target);
        Vector3f direction = Vector3f.subtract(target, position).normalize();
        rawPosition[0] = position.getX();
        rawPosition[1] = position.getY();
        rawPosition[2] = position.getZ();

        right = Vector3f.cross(direction, Vector3f.UP).normalize();
        up = Vector3f.cross(right, direction).normalize();
        viewMatrix = Matrix4f.lookAt(
                position,
                target,
                Vector3f.UP
        );
    }

    @Override
    public void calculateProjectionMatrix()
    {
        projectionMatrix = Matrix4f.perspective(fov, aspectRatio, near, far);
    }

    private void addZoom(float zoom)
    {
        this.zoom += zoom;
        if (this.zoom < 0.1) this.zoom = 0.1f;
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
        this.yaw += yaw;
        this.yaw %= 360f;
        calculateViewMatrix();
    }

    private void addPitch(float pitch)
    {
        this.pitch += pitch;
        if (this.pitch > 89f) this.pitch = 89f;
        if (this.pitch < -89f) this.pitch = -89f;
        calculateViewMatrix();
    }
}