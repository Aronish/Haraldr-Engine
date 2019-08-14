package com.game;

import com.game.event.EventType;
import com.game.event.KeyEvent;
import com.game.gameobject.Player;
import com.game.world.World;
import com.game.physics.PlayerMovementType;

import static com.game.Application.MAIN_LOGGER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class KeyInputHandler {

    /**
     * These key checks are queried every frame. Using glfwGetKey, the state of the key at that time is checked.
     * This is better for movement, etc. because of the way key repeating works. Makes for smooth movement.
     * @param camera the current camera.
     * @param window the current window.
     * @param deltaTime the delta time.
     * @param player the current player.
     * @param world the current world
     */
    public void processInput(Camera camera, long window, float deltaTime, Player player, World world){
        player.setMovementType(PlayerMovementType.STAND);
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) != 0){
            player.setRunning(true);
        }else{
            player.setRunning(false);
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) != 0){
            player.setBoosting(true);
        }else{
            player.setBoosting(false);
        }
        if (glfwGetKey(window, GLFW_KEY_A) != 0) {
            player.setMovementType(PlayerMovementType.LEFT);
        }
        if (glfwGetKey(window, GLFW_KEY_D) != 0) {
            player.setMovementType(PlayerMovementType.RIGHT);
        }
        if (glfwGetKey(window, GLFW_KEY_R) != 0){
            player.resetGravityAcceleration();
            player.resetPosition();
            camera.setScale(1.0f);
            camera.setPosition(player.getPosition().add(player.getGameObjectType().model.getAABB().getMiddle()));
        }
        if (glfwGetKey(window, GLFW_KEY_C) != 0){
            player.setHasGravity(false);
        }
        if (glfwGetKey(window, GLFW_KEY_V) != 0){
            player.setHasGravity(true);
        }
        if (glfwGetKey(window, GLFW_KEY_Z) != 0){
            world.resetNoiseScale();
        }
        if (glfwGetKey(window, GLFW_KEY_UP) != 0){
            camera.calculateScale(true, deltaTime);
        }
        if (glfwGetKey(window, GLFW_KEY_DOWN) != 0){
            camera.calculateScale(false, deltaTime);
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT) != 0){
            world.increaseNoiseScale(0.001d);
        }
        if (glfwGetKey(window, GLFW_KEY_RIGHT) != 0){
            world.increaseNoiseScale(-0.001d);
        }
        if (glfwGetKey(window, GLFW_KEY_SPACE) != 0) {
            if (!player.isFalling()) {
                player.setJumping(true);
                player.setFalling(true);
            }
        }
        if (glfwGetKey(window, GLFW_KEY_G) != 0){
            world.regenerateWorld();
        }
    }

    /**
     * Processes a key event immediately. Does not repeat.
     * @param window the current window.
     * @param event the key event.
     */
    public void processKeyEvent(Window window, KeyEvent event){
        if (event.eventType == EventType.KEY_PRESSED){
            MAIN_LOGGER.info(event.toString());
            if (event.keyCode == GLFW_KEY_ESCAPE){
                glfwSetWindowShouldClose(window.getWindow(), true);
            }
            if (event.keyCode == GLFW_KEY_F){
                window.changeFullscreen();
            }
            if (event.keyCode == GLFW_KEY_B){
                window.setVSync(!Main.getApplication().getWindow().VSyncOn());
                MAIN_LOGGER.info("VSync: " + window.VSyncOn());
            }
        }
    }
}
