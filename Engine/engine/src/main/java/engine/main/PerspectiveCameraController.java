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
    private static final float MOVEMENT_SENSITIVITY = 0.1f;
    private static final float ZOOM_SENSITIVITY = 2f;

    private float lastX, lastY;

    public void handleRotation(@NotNull PerspectiveCamera camera, @NotNull MouseMovedEvent event)
    {
        camera.addYaw(((float) event.xPos - lastX) * MOVEMENT_SENSITIVITY);
        camera.addPitch((lastY - (float) event.yPos) * MOVEMENT_SENSITIVITY);
        lastX = (float) event.xPos;
        lastY = (float) event.yPos;
    }

    public void handleScroll(@NotNull MouseScrolledEvent event)
    {
        Matrix4f.addZoom((float) -event.yOffset * ZOOM_SENSITIVITY);
    }

    public void handleMovement(PerspectiveCamera camera, long window, float deltaTime)
    {
        if (Input.isKeyPressed(window, KEY_W))
        {
            camera.addPosition(camera.getDirection().multiply(CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_S))
        {
            camera.addPosition(camera.getDirection().multiply(-CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_D))
        {
            camera.addPosition(Vector3f.normalize(Vector3f.cross(camera.getDirection(), PerspectiveCamera.up)).multiply(CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_A))
        {
            camera.addPosition(Vector3f.normalize(Vector3f.cross(camera.getDirection(), PerspectiveCamera.up)).multiply(-CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_SPACE))
        {
            camera.addPosition(Vector3f.normalize(Vector3f.cross(Vector3f.normalize(Vector3f.cross(camera.getDirection(), PerspectiveCamera.up)), camera.getDirection())).multiply(CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_C))
        {
            camera.addPosition(Vector3f.normalize(Vector3f.cross(Vector3f.normalize(Vector3f.cross(camera.getDirection(), PerspectiveCamera.up)), camera.getDirection())).multiply(-CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_R))
        {
            camera.setPosition(new Vector3f(0f, 0f, -5f));
            Matrix4f.resetZoom();
        }
    }
}