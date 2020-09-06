package oldgame.main;

import haraldr.debug.Logger;
import haraldr.event.EventType;
import haraldr.event.KeyEvent;
import haraldr.event.MouseScrolledEvent;
import haraldr.input.Input;
import haraldr.input.KeyboardKey;
import haraldr.main.Window;
import haraldr.math.Vector3f;
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
    public void processInput(GameCamera camera, Window window, @NotNull Player player, World world)
    {
        player.setMovementType(PlayerMovementType.STAND);
        player.setRunning(Input.isKeyPressed(window, KeyboardKey.KEY_LEFT_SHIFT));
        player.setBoosting(Input.isKeyPressed(window, KeyboardKey.KEY_LEFT_CONTROL));
        if (Input.isKeyPressed(window, KeyboardKey.KEY_A))
        {
            player.setMovementType(PlayerMovementType.LEFT);
        }
        if (Input.isKeyPressed(window, KeyboardKey.KEY_D))
        {
            player.setMovementType(PlayerMovementType.RIGHT);
        }
        if (Input.isKeyPressed(window, KeyboardKey.KEY_R))
        {
            player.resetGravityAcceleration();
            player.resetPosition();
            camera.setScale(1.0f);
            camera.setPosition(Vector3f.add(player.getPosition(), player.getGameObjectType().getModel().getAABB().getMiddle()));
        }
        if (Input.isKeyPressed(window, KeyboardKey.KEY_C))
        {
            player.setHasGravity(false);
        }
        if (Input.isKeyPressed(window, KeyboardKey.KEY_V))
        {
            player.resetGravityAcceleration();
            player.setHasGravity(true);
        }
        if (Input.isKeyPressed(window, KeyboardKey.KEY_Z))
        {
            world.resetNoiseScale();
        }
        if (Input.isKeyPressed(window, KeyboardKey.KEY_LEFT))
        {
            world.increaseNoiseScale(0.001d);
        }
        if (Input.isKeyPressed(window, KeyboardKey.KEY_RIGHT))
        {
            world.increaseNoiseScale(-0.001d);
        }
        if (Input.isKeyPressed(window, KeyboardKey.KEY_SPACE))
        {
            if (!player.isFalling())
            {
                player.setJumping(true);
                player.setFalling(true);
            }
        }
        if (Input.isKeyPressed(window, KeyboardKey.KEY_G))
        {
            world.generateWorld();
        }
        if (Input.isKeyPressed(window, KeyboardKey.KEY_H))
        {
            world.getGrid().clear();
        }
    }

    public void processKeyEvent(@NotNull KeyEvent event, Window window)
    {
        if (event.eventType == EventType.KEY_PRESSED)
        {
            if (event.keyCode == KeyboardKey.KEY_B.keyCode)
            {
                window.setVSync(!window.vSyncOn());
                Logger.info("VSync: " + window.vSyncOn());
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
