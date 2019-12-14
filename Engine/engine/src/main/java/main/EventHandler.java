package main;

import event.EventType;
import event.KeyEvent;
import event.MouseScrolledEvent;
import gameobject.Player;
import org.jetbrains.annotations.NotNull;
import physics.PlayerMovementType;
import world.World;

import static main.Application.MAIN_LOGGER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class EventHandler
{
    /**
     * These key checks are queried every frame. Using glfwGetKey, the state of the key at that time is checked.
     * This is better for movement, etc. because of the way key repeating works. Makes for smooth movement.
     * @param camera the current camera.
     * @param window the current window.
     * @param player the current player.
     * @param world the current world
     */
    public void processInput(Camera camera, long window, @NotNull Player player, World world)
    {
        player.setMovementType(PlayerMovementType.STAND);
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) != 0)
        {
            player.setRunning(true);
        }else{
            player.setRunning(false);
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) != 0)
        {
            player.setBoosting(true);
        }else{
            player.setBoosting(false);
        }
        if (glfwGetKey(window, GLFW_KEY_A) != 0)
        {
            player.setMovementType(PlayerMovementType.LEFT);
        }
        if (glfwGetKey(window, GLFW_KEY_D) != 0)
        {
            player.setMovementType(PlayerMovementType.RIGHT);
        }
        if (glfwGetKey(window, GLFW_KEY_R) != 0)
        {
            player.resetGravityAcceleration();
            player.resetPosition();
            camera.setScale(1.0f);
            camera.setPosition(player.getPosition().addReturn(player.getGameObjectType().getModel().getAABB().getMiddle()));
        }
        if (glfwGetKey(window, GLFW_KEY_C) != 0)
        {
            player.setHasGravity(false);
        }
        if (glfwGetKey(window, GLFW_KEY_V) != 0)
        {
            player.resetGravityAcceleration();
            player.setHasGravity(true);
        }
        if (glfwGetKey(window, GLFW_KEY_Z) != 0)
        {
            world.resetNoiseScale();
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT) != 0)
        {
            world.increaseNoiseScale(0.001d);
        }
        if (glfwGetKey(window, GLFW_KEY_RIGHT) != 0)
        {
            world.increaseNoiseScale(-0.001d);
        }
        if (glfwGetKey(window, GLFW_KEY_SPACE) != 0)
        {
            if (!player.isFalling())
            {
                player.setJumping(true);
                player.setFalling(true);
            }
        }
        if (glfwGetKey(window, GLFW_KEY_G) != 0)
        {
            world.generateWorld();
        }
        if (glfwGetKey(window, GLFW_KEY_H) != 0)
        {
            world.getGrid().clear();
        }
    }

    /**
     * Processes a key event immediately. Does not repeat.
     * @param window the current window.
     * @param event the key event.
     */
    public void processKeyEvent(@NotNull KeyEvent event, Window window)
    {
        if (event.eventType == EventType.KEY_PRESSED)
        {
            if (event.keyCode == GLFW_KEY_B)
            {
                window.setVSync(!window.VSyncOn());
                MAIN_LOGGER.info("VSync: " + window.VSyncOn());
            }
        }
    }

    public void processScrollEvent(@NotNull MouseScrolledEvent event, Camera camera)
    {
        if (event.yOffset < 0)
        {
            camera.zoomOut();
        }else{
            camera.zoomIn();
        }
    }
}
