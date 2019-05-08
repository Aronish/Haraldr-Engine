package main.java;

import main.java.level.Player;
import main.java.math.Vector3f;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_N;
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
    private static boolean renderDebug = false;

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
    static void moveCameraAndPlayer(double deltaTime, Player player){
        if(keys[GLFW_KEY_LEFT_SHIFT]){
            player.setVelocity(10.0d, 10.0d);
        }else{
            player.setVelocity(5.0d, 5.0d);
        }
        if(keys[GLFW_KEY_W]) {
            player.calculateYPosition(true, deltaTime);
            Camera.setPosition(player.getPosition().multiply(Camera.scale));
        }
        if(keys[GLFW_KEY_A]) {
            player.calculateXPosition(false, deltaTime);
            Camera.setPosition(player.getPosition().multiply(Camera.scale));
        }
        if(keys[GLFW_KEY_S]){
            player.calculateYPosition(false, deltaTime);
            Camera.setPosition(player.getPosition().multiply(Camera.scale));
        }
        if(keys[GLFW_KEY_D]) {
            player.calculateXPosition(true, deltaTime);
            Camera.setPosition(player.getPosition().multiply(Camera.scale));
        }
        if(keys[GLFW_KEY_R]){
            player.setPosition(new Vector3f());
            Camera.scale = 1.0f;
            Camera.setPosition(new Vector3f());
        }
        if(keys[GLFW_KEY_UP]){
            Camera.calculateScale(true, deltaTime);
            Camera.setPosition(player.getPosition().multiply(Camera.scale));
        }
        if(keys[GLFW_KEY_DOWN]){
            Camera.calculateScale(false, deltaTime);
            Camera.setPosition(player.getPosition().multiply(Camera.scale));
        }
        if(keys[GLFW_KEY_M]){
            setRenderDebug(true);
        }
        if(keys[GLFW_KEY_N]){
            setRenderDebug(false);
        }
    }

    private static void setRenderDebug(boolean shouldRenderDebug){
        renderDebug = shouldRenderDebug;
    }

    public static boolean shouldRenderDebug(){
        return renderDebug;
    }
}
