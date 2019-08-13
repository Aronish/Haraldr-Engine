package com.game.physics;

import com.game.Camera;
import com.game.level.Player;
import com.game.level.gameobject.tile.Tile;
import com.game.math.Vector3f;

import static com.game.physics.Direction.EAST;
import static com.game.physics.Direction.INVALIDDIR;
import static com.game.physics.Direction.NORTH;
import static com.game.physics.Direction.SOUTH;
import static com.game.physics.Direction.WEST;

/**
 * Class that handles collision detection.
 */
public class CollisionDetector {

    /**
     * Does all the collision related operations.
     * @param tile the Entity, whose TexturedModel is currently checked.
     * @param player the Player to check collisions with.
     */
    public static void doCollisions(Tile tile, Player player){
        if (checkCollision(tile, player)){
            resolveCollision(getCollisionDirection(tile, player), player);
        }
    }

    /**
     * Checks whether there is a collision between the Player in the Level and the current TexturedModel in the Entity.
     * @param player the Player to check collisions with.
     * @param tile the Entity, whose TexturedModel is currently checked.
     * @return true if there was a collision on both axes.
     */
    private static boolean checkCollision(Tile tile, Player player){
        boolean collisionX = player.getPosition().getX() + player.getGameObjectType().model.getAABB().getWidth() > tile.getPosition().getX() && tile.getPosition().getX() + tile.getGameObjectType().model.getAABB().getWidth() > player.getPosition().getX();
        boolean collisionY = player.getPosition().getY() - player.getGameObjectType().model.getAABB().getHeight() < tile.getPosition().getY() && tile.getPosition().getY() - tile.getGameObjectType().model.getAABB().getHeight() < player.getPosition().getY();
        return collisionX && collisionY;
    }

    /**
     * Gets the direction, in which the collision most likely happened.
     * @param player the Player to check collisions with.
     * @param tile the Entity, whose TexturedModel is currently checked.
     * @return a custom data pair with the direction and overlap distance.
     */
    private static CollisionDataMap getCollisionDirection(Tile tile, Player player){
        float topCollision = tile.getPosition().getY() - (player.getPosition().getY() - player.getGameObjectType().model.getAABB().getHeight());
        float rightCollision = tile.getPosition().getX() + tile.getGameObjectType().model.getAABB().getWidth() - player.getPosition().getX();
        float leftCollision = player.getPosition().getX() + player.getGameObjectType().model.getAABB().getWidth() - tile.getPosition().getX();
        float bottomCollision = player.getPosition().getY() - (tile.getPosition().getY() - tile.getGameObjectType().model.getAABB().getHeight());

        if (topCollision < bottomCollision && topCollision < leftCollision && topCollision < rightCollision ) {
            return new CollisionDataMap(NORTH, topCollision);
        }else if (rightCollision < leftCollision && rightCollision < topCollision && rightCollision < bottomCollision ) {
            return new CollisionDataMap(EAST, rightCollision);
        }else if (leftCollision < rightCollision && leftCollision < topCollision && leftCollision < bottomCollision) {
            return new CollisionDataMap(WEST, leftCollision);
        }else if (bottomCollision < topCollision && bottomCollision < leftCollision && bottomCollision < rightCollision) {
            return new CollisionDataMap(SOUTH, bottomCollision);
        }else{
            return new CollisionDataMap(INVALIDDIR, 0.0f);
        }
    }

    /**
     * Responds to a collision by displacing the Player with the overlap distance.
     * @param collisionDataMap the data pair with direction and overlap distance.
     * @param player the player that collided.
     */
    private static void resolveCollision(CollisionDataMap collisionDataMap, Player player) {
        float inside = collisionDataMap.getInside();
        switch (collisionDataMap.getCollisionDirection()) {
            case NORTH:
                if (!player.isJumping()){
                    player.resetGravityAcceleration();
                    player.setFalling(false);
                }
                player.addPosition(new Vector3f(0.0f, inside));
                Camera.addPosition(new Vector3f(0.0f, inside * Camera.scale));
                break;
            case EAST:
                player.addPosition(new Vector3f(inside, 0.0f));
                Camera.addPosition(new Vector3f(inside * Camera.scale, 0.0f));
                break;
            case WEST:
                player.addPosition(new Vector3f(-inside, 0.0f));
                Camera.addPosition(new Vector3f(-inside * Camera.scale, 0.0f));
                break;
            case SOUTH:
                player.addPosition(new Vector3f(0.0f, -inside));
                Camera.addPosition(new Vector3f(0.0f, -inside * Camera.scale));
                break;
        }
    }
}
