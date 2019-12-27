package sandbox;

import engine.input.Input;
import engine.main.OrthograhpicCamera;
import engine.math.Vector3f;

import static engine.input.Key.KEY_A;
import static engine.input.Key.KEY_D;
import static engine.input.Key.KEY_R;
import static engine.input.Key.KEY_S;
import static engine.input.Key.KEY_W;

public class EventHandler
{
    private static final float CAMERA_SPEED = 5f;

    public static void processInput(OrthograhpicCamera camera, long window, float deltaTime)
    {
        if (Input.isKeyPressed(window, KEY_A))
        {
            camera.addPosition(new Vector3f(-CAMERA_SPEED * deltaTime, 0.0f));
        }
        if (Input.isKeyPressed(window, KEY_D))
        {
            camera.addPosition(new Vector3f(CAMERA_SPEED * deltaTime, 0.0f));
        }
        if (Input.isKeyPressed(window, KEY_W))
        {
            camera.addPosition(new Vector3f(0.0f, CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_S))
        {
            camera.addPosition(new Vector3f(0.0f, -CAMERA_SPEED * deltaTime));
        }
        if (Input.isKeyPressed(window, KEY_R))
        {
            camera.setPosition(new Vector3f());
        }
    }
}
