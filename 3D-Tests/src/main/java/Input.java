package main.java;

import main.java.debug.Logger;
import main.java.level.Player;
import main.java.level.World;
import main.java.math.Vector3f;
import main.java.physics.EnumPlayerMovementType;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_N;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
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
            if (key > 0){
                keys[key] = true;
            }else{
                Logger.setWarningLevel();
                Logger.log("Invalid keycode: " + key);
            }
        }else if (action == GLFW_RELEASE){
            if (key > 0){
                keys[key] = false;
            }else{
                Logger.setWarningLevel();
                Logger.log("Invalid keycode: " + key);
            }
        }
    }

    /**
     * Performs operations based on the keys pressed.
     * @param deltaTime the delta time gotten from the timing circuit in Main.
     * @param player the Player that should move with the Camera.
     * @param world the World which, in this case is used to regenerate the World.
     */
    public static void processInput(float deltaTime, Player player, World world){
        player.setMovementType(EnumPlayerMovementType.STAND);
        if(keys[GLFW_KEY_LEFT_SHIFT]){
            player.setRunning(true);
        }else{
            player.setRunning(false);
        }
        if(keys[GLFW_KEY_LEFT_CONTROL]){
            player.setBoosting(true);
        }else{
            player.setBoosting(false);
        }
        if(keys[GLFW_KEY_A]) {
            player.setMovementType(EnumPlayerMovementType.LEFT);
        }
        if(keys[GLFW_KEY_D]) {
            player.setMovementType(EnumPlayerMovementType.RIGHT);
        }
        if(keys[GLFW_KEY_R]){
            player.resetGravityAcceleration();
            player.resetPosition();
            Camera.scale = 1.0f;
            Camera.setPosition(player.getPosition());
        }
        if(keys[GLFW_KEY_C]){
            player.setHasGravity(false);
        }
        if(keys[GLFW_KEY_V]){
            player.setHasGravity(true);
        }
        if(keys[GLFW_KEY_Z]){
            world.resetNoiseScale();
        }
        if(keys[GLFW_KEY_UP]){
            Camera.calculateScale(true, deltaTime);
        }
        if(keys[GLFW_KEY_DOWN]){
            Camera.calculateScale(false, deltaTime);
        }
        if(keys[GLFW_KEY_LEFT]){
            world.increaseNoiseScale(0.001d);
        }
        if(keys[GLFW_KEY_RIGHT]){
            world.increaseNoiseScale(-0.001d);
        }
        if(keys[GLFW_KEY_M]){
            setDebugEnabled(true);
        }
        if(keys[GLFW_KEY_N]){
            setDebugEnabled(false);
        }
        if(keys[GLFW_KEY_SPACE]) {
            if (!player.isFalling()) {
                player.setJumping(true);
                player.setFalling(true);
            }
        }
        if(keys[GLFW_KEY_G]){
            world.regenerateWorld();
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
