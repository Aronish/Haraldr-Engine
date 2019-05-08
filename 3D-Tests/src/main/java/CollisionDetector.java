package main.java;

import main.java.graphics.TexturedModel;
import main.java.level.Entity;
import main.java.level.Level;
import main.java.level.Player;
import main.java.math.Vector3f;

import static main.java.EnumDirection.EAST;
import static main.java.EnumDirection.NORTH;
import static main.java.EnumDirection.SOUTH;
import static main.java.EnumDirection.WEST;

class CollisionDetector {

    static boolean checkCollision(Level level, Entity entity, TexturedModel texturedModel){
        Player player = level.getPlayer();
        boolean collisionX = player.getPosition().x + player.getWidth() > entity.getPosition().x + texturedModel.getRelativePosition().x && entity.getPosition().x + texturedModel.getRelativePosition().x + texturedModel.getAABB().getWidth() > player.getPosition().x;
        boolean collisionY = player.getPosition().y - player.getHeight() < entity.getPosition().y + texturedModel.getRelativePosition().y && entity.getPosition().y + texturedModel.getRelativePosition().y - texturedModel.getAABB().getHeight() < player.getPosition().y;
        return collisionX && collisionY;
    }

    static CollisionDataMap getCollisionDirection(Level level, Entity entity, TexturedModel texturedModel){
        Player player = level.getPlayer();
        float topCollision = (entity.getPosition().y + texturedModel.getRelativePosition().y) - (player.getPosition().y - player.getHeight());
        float rightCollision = (entity.getPosition().x + texturedModel.getRelativePosition().x) + texturedModel.getAABB().getWidth() - player.getPosition().x;
        float leftCollision = player.getPosition().x + player.getWidth() - (entity.getPosition().x + texturedModel.getRelativePosition().x);
        float bottomCollision = player.getPosition().y - ((entity.getPosition().y + texturedModel.getRelativePosition().y) - texturedModel.getAABB().getHeight());
        CollisionDataMap collisionDataMap = new CollisionDataMap();
        if (topCollision < bottomCollision && topCollision < leftCollision && topCollision < rightCollision ) {
            collisionDataMap.setData(NORTH, topCollision);
        }
        if (rightCollision < leftCollision && rightCollision < topCollision && rightCollision < bottomCollision ) {
            collisionDataMap.setData(EAST, rightCollision);
        }
        if (leftCollision < rightCollision && leftCollision < topCollision && leftCollision < bottomCollision) {
            collisionDataMap.setData(WEST, leftCollision);
        }
        if (bottomCollision < topCollision && bottomCollision < leftCollision && bottomCollision < rightCollision) {
            collisionDataMap.setData(SOUTH, bottomCollision);
        }
        return collisionDataMap;
    }

    static void doCollision(CollisionDataMap collisionDataMap, Level level, Entity entity, TexturedModel texturedModel) {
        float inside = collisionDataMap.getInside();
        Player player = level.getPlayer();
        switch (collisionDataMap.getCollisionDirection()) {
            case NORTH:
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
