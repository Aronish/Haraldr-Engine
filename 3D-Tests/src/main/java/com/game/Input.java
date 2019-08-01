package com.game;

import com.game.debug.Logger;
import com.game.level.Grid;
import com.game.level.Player;
import com.game.level.World;
import com.game.physics.PlayerMovementType;
import org.lwjgl.glfw.GLFWKeyCallback;

import static com.game.Application.MAIN_LOGGER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_I;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
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
//TODO: NEW EVENT SYSTEM WILL MAKE THIS CLASS OBSOLETE.
public class Input extends GLFWKeyCallback {

    private static boolean[] keys = new boolean[65536];
    private static boolean instancedRendering = true;
    private static boolean shouldCreateMap = false;

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods){
        if (key > 0){ //Key responses won't get repeated if put here instead of in processInput.
            if (action == GLFW_PRESS){
                if (key == GLFW_KEY_ESCAPE){
                    glfwSetWindowShouldClose(window, true);
                }
                if (key == GLFW_KEY_F){
                    Main.getApplication().getWindow().changeFullscreen();
                }
                if (key == GLFW_KEY_I){
                    instancedRendering = !instancedRendering;
                    MAIN_LOGGER.info("Instanced Rendering: " + instancedRendering);
                }
                if (key == GLFW_KEY_M){
                    shouldCreateMap = true;
                }
                if (key == GLFW_KEY_B){
                    Main.getApplication().getWindow().setVSync(!Main.getApplication().getWindow().VSyncOn());
                    MAIN_LOGGER.info("VSync: " + Main.getApplication().getWindow().VSyncOn());
                }
                keys[key] = true;
            }else if (action == GLFW_RELEASE){
                keys[key] = false;
            }
        }else{
            MAIN_LOGGER.warn("Invalid keycode: " + key);
        }
    }

    /**
     * Performs operations based on the keys pressed.
     * @param deltaTime the delta time gotten from the timing circuit in Main.
     * @param player the Player that should move with the Camera.
     * @param world the World which, for now, to regenerate.
     */
    public static void processInput(float deltaTime, Player player, World world){
        player.setMovementType(PlayerMovementType.STAND);
        if (keys[GLFW_KEY_LEFT_SHIFT]){
            player.setRunning(true);
        }else{
            player.setRunning(false);
        }
        if (keys[GLFW_KEY_LEFT_CONTROL]){
            player.setBoosting(true);
        }else{
            player.setBoosting(false);
        }
        if (keys[GLFW_KEY_A]) {
            player.setMovementType(PlayerMovementType.LEFT);
        }
        if (keys[GLFW_KEY_D]) {
            player.setMovementType(PlayerMovementType.RIGHT);
        }
        if (keys[GLFW_KEY_R]){
            player.resetGravityAcceleration();
            player.resetPosition();
            Camera.setScale(1.0f);
            Camera.setPosition(player.getPosition().add(player.getGameObjectType().model.getAABB().getMiddle()));
        }
        if (keys[GLFW_KEY_C]){
            player.setHasGravity(false);
        }
        if (keys[GLFW_KEY_V]){
            player.setHasGravity(true);
        }
        if (keys[GLFW_KEY_Z]){
            world.resetNoiseScale();
        }
        if (keys[GLFW_KEY_UP]){
            Camera.calculateScale(true, deltaTime);
        }
        if (keys[GLFW_KEY_DOWN]){
            Camera.calculateScale(false, deltaTime);
        }
        if (keys[GLFW_KEY_LEFT]){
            world.increaseNoiseScale(0.001d);
        }
        if (keys[GLFW_KEY_RIGHT]){
            world.increaseNoiseScale(-0.001d);
        }
        if (keys[GLFW_KEY_SPACE]) {
            if (!player.isFalling()) {
                player.setJumping(true);
                player.setFalling(true);
            }
        }
        if (keys[GLFW_KEY_G]){
            world.regenerateWorld();
        }
        if (shouldCreateMap){
            WorldMapper.generateWorldMap(world.getGrid().getWidth() * Grid.GRID_SIZE, world.getGrid().getHeight() * Grid.GRID_SIZE, world.getGrid(), "src/main/resources/maps/test.png");
            shouldCreateMap = false;
        }
    }

    /**
     * @return whether the game should be rendered with instancing.
     */
    public static boolean usingInstancedRendering(){
        return instancedRendering;
    }
}
