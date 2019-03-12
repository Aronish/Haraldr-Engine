package main.java;

import main.java.math.Vector3f;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

public class Input extends GLFWKeyCallback {

    private static boolean[] keys = new boolean[65536];

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods){
        if (action == GLFW_PRESS){
            if (key == GLFW_KEY_ESCAPE){
                glfwSetWindowShouldClose(window, true);
            }
            keys[key] = true;
        }else if (action == GLFW_RELEASE){
            keys[key] = false;
        }
    }

    static void moveCamera(double deltaTime){
        if(keys[GLFW_KEY_LEFT_SHIFT]){
            Camera.velocity = 10.0d;
        }else{
            Camera.velocity = 5.0d;
        }
        if(keys[GLFW_KEY_W]) {
            Camera.calculateYPosition(false, deltaTime);
        }
        if(keys[GLFW_KEY_A]) {
            Camera.calculateXPosition(false, deltaTime);
        }
        if(keys[GLFW_KEY_S]){
            Camera.calculateYPosition(true, deltaTime);
        }
        if(keys[GLFW_KEY_D]) {
            Camera.calculateXPosition(true, deltaTime);
        }
        if(keys[GLFW_KEY_R]){
            Camera.setPos(new Vector3f(0.0f, 0.0f, 0.0f));
        }
    }
}
