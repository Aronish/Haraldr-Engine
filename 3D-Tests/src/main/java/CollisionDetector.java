package main.java;

import main.java.graphics.TexturedModel;
import main.java.math.Vector3f;

class CollisionDetector {

    static boolean checkCollision(Level level, Entity entity, TexturedModel texturedModel){
        Player player = level.getPlayer();
        boolean collisionX = player.getPosition().x + player.getWidth() > entity.getPosition().x + texturedModel.getRelativePosition().x && entity.getPosition().x + texturedModel.getRelativePosition().x + texturedModel.getAABB().getWidth() > player.getPosition().x;
        boolean collisionY = player.getPosition().y - player.getHeight() < entity.getPosition().y + texturedModel.getRelativePosition().y && entity.getPosition().y + texturedModel.getRelativePosition().y - texturedModel.getAABB().getHeight() < player.getPosition().y;
        return collisionX && collisionY;
    }

    static EnumDirection getCollisionDirection(Level level, Entity entity, TexturedModel texturedModel){
        Player player = level.getPlayer();
        float topCollision = (entity.getPosition().y + texturedModel.getRelativePosition().y) - (player.getPosition().y - player.getHeight());
        float rightCollision = (entity.getPosition().x + texturedModel.getRelativePosition().x) + texturedModel.getAABB().getWidth() - player.getPosition().x;
        float leftCollision = player.getPosition().x + player.getWidth() - (entity.getPosition().x + texturedModel.getRelativePosition().x);
        float bottomCollision = player.getPosition().y - ((entity.getPosition().y + texturedModel.getRelativePosition().y) - texturedModel.getAABB().getHeight());
        if (topCollision < bottomCollision && topCollision < leftCollision && topCollision < rightCollision ) {
            return EnumDirection.NORTH;
        }
        if (rightCollision < leftCollision && rightCollision < topCollision && rightCollision < bottomCollision ) {
            return EnumDirection.EAST;
        }
        if (leftCollision < rightCollision && leftCollision < topCollision && leftCollision < bottomCollision) {
            return EnumDirection.WEST;
        }
        if (bottomCollision < topCollision && bottomCollision < leftCollision && bottomCollision < rightCollision) {
            return EnumDirection.SOUTH;
        }
        return EnumDirection.INVALIDDIR;
    }

    static void doCollision(EnumDirection direction, Level level, Entity entity, TexturedModel texturedModel) {
        float inside;
        Player player = level.getPlayer();
        switch (direction) {
            case NORTH:
                inside = (entity.getPosition().y + texturedModel.getRelativePosition().y) - (player.getPosition().y - player.getHeight());
                player.addPosition(new Vector3f(0.0f, inside));
                Camera.addPosition(new Vector3f(0.0f, inside * Camera.scale));
                break;
            case EAST:
                inside = (entity.getPosition().x + texturedModel.getRelativePosition().x) + texturedModel.getAABB().getWidth() - player.getPosition().x;
                player.addPosition(new Vector3f(inside, 0.0f));
                Camera.addPosition(new Vector3f(inside * Camera.scale, 0.0f));
                break;
            case WEST:
                inside = player.getPosition().x + player.getWidth() - (entity.getPosition().x + texturedModel.getRelativePosition().x);
                player.addPosition(new Vector3f(-inside, 0.0f));
                Camera.addPosition(new Vector3f(-inside * Camera.scale, 0.0f));
                break;
            case SOUTH:
                inside = player.getPosition().y - ((entity.getPosition().y + texturedModel.getRelativePosition().y) - texturedModel.getAABB().getHeight());
                player.addPosition(new Vector3f(0.0f, -inside));
                Camera.addPosition(new Vector3f(0.0f, -inside * Camera.scale));
                break;
        }
    }
}
