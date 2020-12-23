package haraldr.scene;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.WindowResizedEvent;
import haraldr.input.Input;
import haraldr.input.KeyboardKey;
import haraldr.input.MouseButton;
import haraldr.main.Window;
import haraldr.math.Matrix4f;
import haraldr.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class OrbitalCamera extends Camera
{
    private static final float MOVE_SPEED = 4f;

    private Vector3f right = new Vector3f();
    private Vector3f up = new Vector3f();
    private Vector3f target = new Vector3f(1f);
    private float pitch, lastPitch, yaw, fov = 60f, near = 0.1f, far = 100f, zoom = 1f, lastX, lastY;
    private boolean rotating, moving, movingDirectional;

    public OrbitalCamera(float width, float height)
    {
        aspectRatio = width / height;
        calculateViewMatrix();
        calculateProjectionMatrix();
    }

    @Override
    public void onUpdate(float deltaTime, Window window)
    {
    }

    @Override
    public void onEvent(Event event, Window window, boolean movable)
    {
        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            var windowResizedEvent = (WindowResizedEvent) event;
            aspectRatio = (float) windowResizedEvent.width / (float) windowResizedEvent.height;
            calculateProjectionMatrix();
        }
        if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1)) rotating = true;
        if (Input.wasMouseReleased(event, MouseButton.MOUSE_BUTTON_1)) rotating = false;
        if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_2)) moving = true;
        if (Input.wasMouseReleased(event, MouseButton.MOUSE_BUTTON_2)) moving = false;
        if (Input.wasKeyPressed(event, KeyboardKey.KEY_LEFT_SHIFT)) movingDirectional = true;
        if (Input.wasKeyReleased(event, KeyboardKey.KEY_LEFT_SHIFT)) movingDirectional = false;
        if (movable && event.eventType == EventType.MOUSE_MOVED)
        {
            var mouseMovedEvent = (MouseMovedEvent) event;
            if (rotating)
            {
                float yaw = (float) (mouseMovedEvent.xPos - lastX) / window.getWidth() * 360f;
                float pitch = (float) (mouseMovedEvent.yPos) / window.getHeight() * 180f - 90f;
                addYaw(yaw);
                addPitch(pitch - lastPitch);
            }
            if (moving)
            {
                if (movingDirectional)
                {
                    zoom += (float) (mouseMovedEvent.yPos - lastY) / window.getHeight() * MOVE_SPEED;
                    if (zoom <= 0.0001f) zoom = 0.0001f;
                } else
                {
                    target.add(Vector3f.add(
                            Vector3f.multiply(right, (float) (lastX - mouseMovedEvent.xPos) / window.getWidth()),
                            Vector3f.multiply(up, (float) (mouseMovedEvent.yPos - lastY) / window.getHeight())
                    ).multiply(MOVE_SPEED));
                }
                calculateViewMatrix();
            }
            lastPitch = (float)(mouseMovedEvent.yPos) / window.getHeight() * 180f - 90f;
            lastX = (float) mouseMovedEvent.xPos;
            lastY = (float) mouseMovedEvent.yPos;
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

    @Override
    public void setPosition(@NotNull Vector3f position)
    {
        target.set(position);
        calculateViewMatrix();
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