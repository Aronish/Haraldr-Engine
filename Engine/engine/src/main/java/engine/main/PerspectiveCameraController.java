package engine.main;

import engine.event.MouseMovedEvent;
import engine.input.Input;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

import static engine.input.Key.KEY_A;
import static engine.input.Key.KEY_D;
import static engine.input.Key.KEY_R;
import static engine.input.Key.KEY_S;
import static engine.input.Key.KEY_W;
import static engine.main.Application.MAIN_LOGGER;

public class PerspectiveCameraController
{
    private static final float CAMERA_SPEED = 5f;

    private float lastX = 640, lastY = 360;

    public void handleRotation(@NotNull PerspectiveCamera camera, @NotNull MouseMovedEvent event, @NotNull Window window)
    {
        float sensitivity = 0.25f;
        float offsetX = (float) event.xPos - lastX;
        float offsetY = lastY - (float) event.yPos;
        lastX = (float) event.xPos;
        lastY = (float) event.yPos;
        offsetX *= sensitivity;
        offsetY *= sensitivity;
        camera.addYaw(offsetX);
        camera.addPitch(offsetY);
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
        if (Input.isKeyPressed(window, KEY_R))
        {
            camera.setPosition(new Vector3f(0f, 0f, -5f));
            Matrix4f.setZoom(1f);
        }
    }
}