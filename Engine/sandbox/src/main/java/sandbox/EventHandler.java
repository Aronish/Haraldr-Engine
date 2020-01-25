package sandbox;

import engine.event.KeyPressedEvent;
import engine.input.Input;
import engine.main.OrthographicCamera;
import engine.main.Window;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static engine.input.Key.KEY_A;
import static engine.input.Key.KEY_D;
import static engine.input.Key.KEY_DOWN;
import static engine.input.Key.KEY_E;
import static engine.input.Key.KEY_R;
import static engine.input.Key.KEY_S;
import static engine.input.Key.KEY_UP;
import static engine.input.Key.KEY_W;

public class EventHandler
{
    private static final float CAMERA_SPEED = 5f;

    @Contract(pure = true)
    public static void onKeyPress(@NotNull KeyPressedEvent event, Window window)
    {
        if (event.keyCode == KEY_E.keyCode)
        {
            window.setFocus(!window.isFocused());
        }
    }

    /////TO BE PUT IN ORTHOGRAPHIC CAMERA CONTROLLER////////////////////////////////////////

    public static void processInput(OrthographicCamera camera, long window, float deltaTime)
    {
        if (Input.isKeyPressed(window, KEY_A))
        {
            camera.addPosition(new Vector3f(-CAMERA_SPEED * deltaTime * Matrix4f.scale, 0.0f));
        }
        if (Input.isKeyPressed(window, KEY_D))
        {
            camera.addPosition(new Vector3f(CAMERA_SPEED * deltaTime * Matrix4f.scale, 0.0f));
        }
        if (Input.isKeyPressed(window, KEY_W))
        {
            camera.addPosition(new Vector3f(0.0f, CAMERA_SPEED * deltaTime * Matrix4f.scale));
        }
        if (Input.isKeyPressed(window, KEY_S))
        {
            camera.addPosition(new Vector3f(0.0f, -CAMERA_SPEED * deltaTime * Matrix4f.scale));
        }
        if (Input.isKeyPressed(window, KEY_R))
        {
            camera.setPosition(new Vector3f());
            Matrix4f.setZoom(1f);
        }
        if (Input.isKeyPressed(window, KEY_UP))
        {
            Matrix4f.addZoom((-1.0f * deltaTime) * Matrix4f.scale);
        }
        if (Input.isKeyPressed(window, KEY_DOWN))
        {
            Matrix4f.addZoom((1.0f * deltaTime) * Matrix4f.scale);
        }
    }
}
