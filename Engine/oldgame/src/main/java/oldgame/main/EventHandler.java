package oldgame.main;

import engine.event.EventType;
import engine.event.KeyEvent;
import engine.event.MouseScrolledEvent;
import engine.input.Input;
import engine.input.Key;
import engine.main.Application;
import engine.main.Window;
import engine.math.Vector3f;
import oldgame.gameobject.Player;
import oldgame.physics.PlayerMovementType;
import oldgame.world.World;
import org.jetbrains.annotations.NotNull;

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
    public void processInput(GameCamera camera, long window, @NotNull Player player, World world)
    {
        player.setMovementType(PlayerMovementType.STAND);
        if (Input.isKeyPressed(window, Key.KEY_LEFT_SHIFT))
        {
            player.setRunning(true);
        }else{
            player.setRunning(false);
        }
        if (Input.isKeyPressed(window, Key.KEY_LEFT_CONTROL))
        {
            player.setBoosting(true);
        }else{
            player.setBoosting(false);
        }
        if (Input.isKeyPressed(window, Key.KEY_A))
        {
            player.setMovementType(PlayerMovementType.LEFT);
        }
        if (Input.isKeyPressed(window, Key.KEY_D))
        {
            player.setMovementType(PlayerMovementType.RIGHT);
        }
        if (Input.isKeyPressed(window, Key.KEY_R))
        {
            player.resetGravityAcceleration();
            player.resetPosition();
            camera.setScale(1.0f);
            camera.setPosition(Vector3f.add(player.getPosition(), player.getGameObjectType().getModel().getAABB().getMiddle()));
        }
        if (Input.isKeyPressed(window, Key.KEY_C))
        {
            player.setHasGravity(false);
        }
        if (Input.isKeyPressed(window, Key.KEY_V))
        {
            player.resetGravityAcceleration();
            player.setHasGravity(true);
        }
        if (Input.isKeyPressed(window, Key.KEY_Z))
        {
            world.resetNoiseScale();
        }
        if (Input.isKeyPressed(window, Key.KEY_LEFT))
        {
            world.increaseNoiseScale(0.001d);
        }
        if (Input.isKeyPressed(window, Key.KEY_RIGHT))
        {
            world.increaseNoiseScale(-0.001d);
        }
        if (Input.isKeyPressed(window, Key.KEY_SPACE))
        {
            if (!player.isFalling())
            {
                player.setJumping(true);
                player.setFalling(true);
            }
        }
        if (Input.isKeyPressed(window, Key.KEY_G))
        {
            world.generateWorld();
        }
        if (Input.isKeyPressed(window, Key.KEY_H))
        {
            world.getGrid().clear();
        }
    }

    public void processKeyEvent(@NotNull KeyEvent event, Window window)
    {
        if (event.eventType == EventType.KEY_PRESSED)
        {
            if (event.keyCode == Key.KEY_B.keyCode)
            {
                window.setVSync(!window.VSyncOn());
                Application.MAIN_LOGGER.info("VSync: " + window.VSyncOn());
            }
        }
    }

    public void processScrollEvent(@NotNull MouseScrolledEvent event, GameCamera camera)
    {
        if (event.yOffset < 0)
        {
            camera.zoomOut();
        }else{
            camera.zoomIn();
        }
    }
}
