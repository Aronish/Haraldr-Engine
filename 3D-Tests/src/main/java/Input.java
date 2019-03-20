package main.java;

import main.java.math.Vector3f;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Class for handling user input. Uses GLFWKeyCallback.
 */
public class Input extends GLFWKeyCallback {

    private static boolean[] keys = new boolean[65536];

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods){
        if (action == GLFW_PRESS){
            if (key == GLFW_KEY_ESCAPE){
                glfwSetWindowShouldClose(window, true);
            }
            if (key == GLFW_KEY_F){
                Main.window.changeFullscreen();
            }
            keys[key] = true;
        }else if (action == GLFW_RELEASE){
            keys[key] = false;
        }
    }

    /**
     * Checks if any keys associated with movement are pressed. If so, the camera is moved accordingly.
     * @param deltaTime the delta time gotten from the timing circuit in Main.
     */
    static void moveCamera(double deltaTime){
        if(keys[GLFW_KEY_LEFT_SHIFT]){
            Camera.velocity = 10.0d;
        }else{
            Camera.velocity = 5.0d;
        }
        if(keys[GLFW_KEY_W]) {
            Camera.calculateYPosition(true, deltaTime);
        }
        if(keys[GLFW_KEY_A]) {
            Camera.calculateXPosition(false, deltaTime);
        }
        if(keys[GLFW_KEY_S]){
            Camera.calculateYPosition(false, deltaTime);
        }
        if(keys[GLFW_KEY_D]) {
            Camera.calculateXPosition(true, deltaTime);
        }
        if(keys[GLFW_KEY_R]){
            Camera.setPosition(new Vector3f(0.0f, 0.0f, 0.0f));
        }
    }
}
