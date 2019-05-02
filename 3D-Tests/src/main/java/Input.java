package main.java;

import main.java.math.Vector3f;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

/**
 * Class for handling user input. Uses GLFWKeyCallback.
 */
public class Input extends GLFWKeyCallback {

    private static boolean[] keys = new boolean[65536];
    private static boolean stateChanging = false;

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
     * @param player the player that should move with the camera.
     */
    static void moveCameraAndPlayer(double deltaTime, Player player, World world){
        setStateChanging(true);
        if(keys[GLFW_KEY_LEFT_SHIFT]){
            Camera.velocity = 10.0d * Camera.scale;
            player.setVelocity(10.0d);
        }else{
            Camera.velocity = 5.0d * Camera.scale;
            player.setVelocity(5.0d);
        }
        if(keys[GLFW_KEY_W]) {
            Camera.calculateYPosition(true, deltaTime);
            player.calculateYPosition(true, deltaTime);
        }
        if(keys[GLFW_KEY_A]) {
            Camera.calculateXPosition(false, deltaTime);
            player.calculateXPosition(false, deltaTime);
        }
        if(keys[GLFW_KEY_S]){
            Camera.calculateYPosition(false, deltaTime);
            player.calculateYPosition(false, deltaTime);
        }
        if(keys[GLFW_KEY_D]) {
            Camera.calculateXPosition(true, deltaTime);
            player.calculateXPosition(true, deltaTime);
        }
        if(keys[GLFW_KEY_R]){
            Camera.setPosition(new Vector3f());
            player.setPosition(new Vector3f());
            world.updateMatrix();
        }
        if(keys[GLFW_KEY_UP]){
            Camera.calculateScale(true, deltaTime);
        }
        if(keys[GLFW_KEY_DOWN]){
            Camera.calculateScale(false, deltaTime);
        }
    }

    /**
     * Sets stateChanging to the specified value. When true, it tells the game that extra logic (matrices, etc.) should be computed.
     * @param changing whether the state should be changing or not.
     */
    static void setStateChanging(boolean changing){
        stateChanging = changing;
    }

    /**
     * Gets the state.
     * @return whether the state is changing or not.
     */
    static boolean isStateChanging(){
        return stateChanging;
    }
}
