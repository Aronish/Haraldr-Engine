package sandbox;

import engine.main.OrthograhpicCamera;
import engine.math.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class EventHandler
{
    private static final float CAMERA_SPEED = 5f;

    public static void processInput(OrthograhpicCamera camera, long window, float deltaTime)
    {
        if (glfwGetKey(window, GLFW_KEY_A) != 0)
        {
            camera.addPosition(new Vector3f(-CAMERA_SPEED * deltaTime, 0.0f));
        }
        if (glfwGetKey(window, GLFW_KEY_D) != 0)
        {
            camera.addPosition(new Vector3f(CAMERA_SPEED * deltaTime, 0.0f));
        }
        if (glfwGetKey(window, GLFW_KEY_W) != 0)
        {
            camera.addPosition(new Vector3f(0.0f, CAMERA_SPEED * deltaTime));
        }
        if (glfwGetKey(window, GLFW_KEY_S) != 0)
        {
            camera.addPosition(new Vector3f(0.0f, -CAMERA_SPEED * deltaTime));
        }
        if (glfwGetKey(window, GLFW_KEY_R) != 0)
        {
            camera.setPosition(new Vector3f());
        }
    }
}
