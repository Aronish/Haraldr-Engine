package engine.main;

import engine.event.MouseMovedEvent;
import engine.event.MouseScrolledEvent;
import engine.input.Input;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

import static engine.input.Key.KEY_A;
import static engine.input.Key.KEY_C;
import static engine.input.Key.KEY_D;
import static engine.input.Key.KEY_R;
import static engine.input.Key.KEY_S;
import static engine.input.Key.KEY_SPACE;
import static engine.input.Key.KEY_W;

public class PerspectiveCameraController
{
    private static final float CAMERA_SPEED = 0.5f;
    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float ZOOM_SENSITIVITY = 2f;

    private PerspectiveCamera reference;
    private float lastX, lastY;

    public PerspectiveCameraController(PerspectiveCamera reference)
    {
        this.reference = reference;
    }

    public void handleRotation(@NotNull MouseMovedEvent event)
    {
        reference.addYaw(((float) event.xPos - lastX) * MOUSE_SENSITIVITY);
        reference.addPitch((lastY - (float) event.yPos) * MOUSE_SENSITIVITY);
        lastX = (float) event.xPos;
        lastY = (float) event.yPos;
    }

    public void handleScroll(@NotNull MouseScrolledEvent event)
    {
        Matrix4f.addZoom((float) -event.yOffset * ZOOM_SENSITIVITY);
    }

    public void handleMovement(Window window, float deltaTime)
    {
        if (Input.isKeyPressed(window, KEY_W))
        {
            reference.addPosition(reference.getDirection().multiply(CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_S))
        {
            reference.addPosition(reference.getDirection().multiply(-CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_D))
        {
            reference.addPosition(Vector3f.normalize(Vector3f.cross(reference.getDirection(), Vector3f.UP)).multiply(CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_A))
        {
            reference.addPosition(Vector3f.normalize(Vector3f.cross(reference.getDirection(), Vector3f.UP)).multiply(-CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_SPACE))
        {
            reference.addPosition(Vector3f.normalize(Vector3f.cross(Vector3f.normalize(Vector3f.cross(reference.getDirection(), Vector3f.UP)), reference.getDirection())).multiply(CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_C))
        {
            reference.addPosition(Vector3f.normalize(Vector3f.cross(Vector3f.normalize(Vector3f.cross(reference.getDirection(), Vector3f.UP)), reference.getDirection())).multiply(-CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_R))
        {
            reference.setPosition(new Vector3f(0f, 0f, -5f));
            Matrix4f.resetZoom();
        }
    }
}