package haraldr.main;

import haraldr.event.MouseMovedEvent;
import haraldr.event.MouseScrolledEvent;
import haraldr.event.WindowFocusEvent;
import haraldr.input.Input;
import haraldr.math.Matrix4f;
import haraldr.math.Vector3f;
import org.jetbrains.annotations.NotNull;

import static haraldr.input.Key.KEY_A;
import static haraldr.input.Key.KEY_C;
import static haraldr.input.Key.KEY_D;
import static haraldr.input.Key.KEY_S;
import static haraldr.input.Key.KEY_SPACE;
import static haraldr.input.Key.KEY_W;

public class PerspectiveCameraController
{
    private static final float CAMERA_SPEED = 0.75f;
    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float ZOOM_SENSITIVITY = 2f;

    private PerspectiveCamera reference;
    private float lastX, lastY;
    private boolean focused = true;

    public PerspectiveCameraController(PerspectiveCamera reference)
    {
        this.reference = reference;
    }

    public void onFocus(@NotNull WindowFocusEvent event)
    {
        focused = event.focused;
    }

    public void handleRotation(@NotNull MouseMovedEvent event)
    {
        if (focused)
        {
            reference.rotate(((float) event.xPos - lastX) * MOUSE_SENSITIVITY, (lastY - (float) event.yPos) * MOUSE_SENSITIVITY);
        }
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

            reference.addPosition(Vector3f.multiply(reference.getDirection(), CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_S))
        {
            reference.addPosition(Vector3f.multiply(reference.getDirection(), -CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_D))
        {
            reference.addPosition(Vector3f.multiply(Vector3f.normalize(Vector3f.cross(reference.getDirection(), Vector3f.UP)), CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_A))
        {
            reference.addPosition(Vector3f.multiply(Vector3f.normalize(Vector3f.cross(reference.getDirection(), Vector3f.UP)), -CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_SPACE))
        {
            reference.addPosition(Vector3f.multiply(Vector3f.normalize(Vector3f.cross(Vector3f.normalize(Vector3f.cross(reference.getDirection(), Vector3f.UP)), reference.getDirection())), CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_C))
        {
            reference.addPosition(Vector3f.multiply(Vector3f.normalize(Vector3f.cross(Vector3f.normalize(Vector3f.cross(reference.getDirection(), Vector3f.UP)), reference.getDirection())), -CAMERA_SPEED * deltaTime));
        }
    }
}