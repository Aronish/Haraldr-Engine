package main.java.physics;

import main.java.Camera;
import main.java.graphics.TexturedModel;
import main.java.level.Entity;
import main.java.level.Player;
import main.java.math.Vector3f;

import static main.java.physics.EnumDirection.EAST;
import static main.java.physics.EnumDirection.INVALIDDIR;
import static main.java.physics.EnumDirection.NORTH;
import static main.java.physics.EnumDirection.SOUTH;
import static main.java.physics.EnumDirection.WEST;

/**
 * Class that handles collision detection.
 */
public class CollisionDetector {

    /**
     * Does all the collision related operations.
     * @param entity the Entity, whose TexturedModel is currently checked.
     * @param texturedModel the TexturedModel to check collision against.
     * @param player the Player to check collisions with.
     */
    public static void doCollisions(Entity entity, TexturedModel texturedModel, Player player){
        if (checkCollision(entity, texturedModel, player)){
            resolveCollision(getCollisionDirection(entity, texturedModel, player), player);
        }
    }

    /**
     * Checks whether there is a collision between the Player in the Level and the current TexturedModel in the Entity.
     * @param player the Player to check collisions with.
     * @param entity the Entity, whose TexturedModel is currently checked.
     * @param texturedModel the TexturedModel to check collision against.
     * @return true if there was a collision on both axes.
     */
    private static boolean checkCollision(Entity entity, TexturedModel texturedModel, Player player){
        boolean collisionX = player.getPosition().x + player.getWidth() > entity.getPosition().x + texturedModel.getRelativePosition().x && entity.getPosition().x + texturedModel.getRelativePosition().x + texturedModel.getAABB().getWidth() > player.getPosition().x;
        boolean collisionY = player.getPosition().y - player.getHeight() < entity.getPosition().y + texturedModel.getRelativePosition().y && entity.getPosition().y + texturedModel.getRelativePosition().y - texturedModel.getAABB().getHeight() < player.getPosition().y;
        return collisionX && collisionY;
    }

    /**
     * Gets the direction, in which the collision most likely happened.
     * @param player the Player to check collisions with.
     * @param entity the Entity, whose TexturedModel is currently checked.
     * @param texturedModel the TexturedModel to check collision against.
     * @return a custom data pair with the direction and overlap distance.
     */
    private static CollisionDataMap getCollisionDirection(Entity entity, TexturedModel texturedModel, Player player){
        float topCollision = (entity.getPosition().y + texturedModel.getRelativePosition().y) - (player.getPosition().y - player.getHeight());
        float rightCollision = (entity.getPosition().x + texturedModel.getRelativePosition().x) + texturedModel.getAABB().getWidth() - player.getPosition().x;
        float leftCollision = player.getPosition().x + player.getWidth() - (entity.getPosition().x + texturedModel.getRelativePosition().x);
        float bottomCollision = player.getPosition().y - ((entity.getPosition().y + texturedModel.getRelativePosition().y) - texturedModel.getAABB().getHeight());

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
            case INVALIDDIR:
                player.resetPosition();
        }
    }
}
