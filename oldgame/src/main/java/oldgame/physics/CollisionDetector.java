package oldgame.physics;

import haraldr.scene.OrthographicCamera;
import haraldr.math.Vector3f;
import oldgame.gameobject.Player;
import oldgame.gameobject.tile.Tile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Class that handles collision detection.
 */
public class CollisionDetector
{
    /**
     * Does all the collision related operations.
     * @param tile the Entity, whose TexturedModel is currently checked.
     * @param player the Player to check collisions with.
     */
    public static void doCollisions(OrthographicCamera camera, Tile tile, Player player)
    {
        if (checkCollision(tile, player))
        {
            resolveCollision(camera, getCollisionDirection(tile, player), player);
        }
    }

    /**
     * Checks whether there is a collision between the Player in the Level and the current TexturedModel in the Entity.
     * @param player the Player to check collisions with.
     * @param tile the Entity, whose TexturedModel is currently checked.
     * @return true if there was a collision on both axes.
     */
    private static boolean checkCollision(@NotNull Tile tile, @NotNull Player player)
    {
        boolean collisionX = player.getPosition().getX() + player.getGameObjectType().getModel().getAABB().getWidth() > tile.getPosition().getX() && tile.getPosition().getX() + tile.getGameObjectType().getModel().getAABB().getWidth() > player.getPosition().getX();
        boolean collisionY = player.getPosition().getY() - player.getGameObjectType().getModel().getAABB().getHeight() < tile.getPosition().getY() && tile.getPosition().getY() - tile.getGameObjectType().getModel().getAABB().getHeight() < player.getPosition().getY();
        return collisionX && collisionY;
    }

    /**
     * Gets the direction, in which the collision most likely happened.
     * @param player the Player to check collisions with.
     * @param tile the Entity, whose TexturedModel is currently checked.
     * @return a custom data pair with the direction and overlap distance.
     */
    @NotNull
    @Contract("_, _ -> new")
    private static CollisionDataMap getCollisionDirection(@NotNull Tile tile, @NotNull Player player)
    {
        float topCollision =    tile.getPosition().getY() - (player.getPosition().getY() - player.getGameObjectType().getModel().getAABB().getHeight());
        float rightCollision =  tile.getPosition().getX() + tile.getGameObjectType().getModel().getAABB().getWidth() - player.getPosition().getX();
        float leftCollision =   player.getPosition().getX() + player.getGameObjectType().getModel().getAABB().getWidth() - tile.getPosition().getX();
        float bottomCollision = player.getPosition().getY() - (tile.getPosition().getY() - tile.getGameObjectType().getModel().getAABB().getHeight());

        if (topCollision < bottomCollision && topCollision < leftCollision && topCollision < rightCollision)            return new CollisionDataMap(Direction.NORTH, topCollision);
        else if (rightCollision < leftCollision && rightCollision < topCollision && rightCollision < bottomCollision)   return new CollisionDataMap(Direction.EAST, rightCollision);
        else if (leftCollision < rightCollision && leftCollision < topCollision && leftCollision < bottomCollision)     return new CollisionDataMap(Direction.WEST, leftCollision);
        else if (bottomCollision < topCollision && bottomCollision < leftCollision && bottomCollision < rightCollision) return new CollisionDataMap(Direction.SOUTH, bottomCollision);
        else                                                                                                            return new CollisionDataMap(Direction.INVALIDDIR, 0.0f);
    }

    /**
     * Responds to a collision by displacing the Player with the overlap distance.
     * @param collisionDataMap the data pair with direction and overlap distance.
     * @param player the player that collided.
     */
    private static void resolveCollision(OrthographicCamera camera, @NotNull CollisionDataMap collisionDataMap, Player player)
    {
        float inside = collisionDataMap.getInside();
        switch (collisionDataMap.getCollisionDirection())
        {
            case NORTH:
                if (!player.isJumping())
                {
                    player.resetGravityAcceleration();
                    player.setFalling(false);
                }
                player.addPosition(new Vector3f(0.0f, inside, 0f));
                //camera.addPosition(new Vector3f(0.0f, inside * camera.getScale(), 0f));
                break;
            case EAST:
                player.addPosition(new Vector3f(inside, 0.0f, 0f));
                //camera.addPosition(new Vector3f(inside * camera.getScale(), 0.0f, 0f));
                break;
            case WEST:
                player.addPosition(new Vector3f(-inside, 0.0f, 0f));
                //camera.addPosition(new Vector3f(-inside * camera.getScale(), 0.0f, 0f));
                break;
            case SOUTH:
                player.addPosition(new Vector3f(0.0f, -inside, 0f));
                //camera.addPosition(new Vector3f(0.0f, -inside * camera.getScale(), 0f));
                break;
        }
    }
}
