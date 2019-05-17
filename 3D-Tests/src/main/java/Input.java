package main.java;

import main.java.level.Player;
import main.java.math.Vector2d;
import main.java.math.Vector3f;
import main.java.physics.EnumPlayerMovementType;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_N;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

/**
 * Class for handling user input. Uses GLFWKeyCallback.
 */
public class Input extends GLFWKeyCallback {

    private static boolean[] keys = new boolean[65536];
    private static boolean debugEnabled = false;

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
    public static void processInput(double deltaTime, Player player){
        player.setStanding();
        if(keys[GLFW_KEY_A]) {
            player.setMovementType(EnumPlayerMovementType.LEFT);
            player.setForces(new Vector2d(-10.0d, 0.0d), deltaTime);
        }
        if(keys[GLFW_KEY_D]) {
            player.setMovementType(EnumPlayerMovementType.RIGHT);
            player.setForces(new Vector2d(10.0d, 0.0d), deltaTime);
        }
        if(keys[GLFW_KEY_R]){
            player.resetForces();
            player.setVelocity(new Vector2d(0.0d, 0.0d));
            player.setPosition(new Vector3f());
            Camera.scale = 1.0f;
            Camera.setPosition(player.getPosition());
        }
        if(keys[GLFW_KEY_UP]){
            Camera.calculateScale(true, deltaTime, player);
        }
        if(keys[GLFW_KEY_DOWN]){
            Camera.calculateScale(false, deltaTime, player);
        }
        if(keys[GLFW_KEY_M]){
            setDebugEnabled(true);
        }
        if(keys[GLFW_KEY_N]){
            setDebugEnabled(false);
        }
    }

    /**
     * Sets whether debug info should be rendered.
     * @param debugEnabled whether debug info should be rendered.
     */
    private static void setDebugEnabled(boolean debugEnabled){
        Input.debugEnabled = debugEnabled;
    }

    /**
     * Gets whether debug info should be rendered.
     */
    public static boolean isDebugEnabled(){
        return debugEnabled;
    }
}
