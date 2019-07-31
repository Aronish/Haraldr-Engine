package com.game.physics;

/**
 * Simple data pair with direction and overlap distance used for collision detection.
 */
class CollisionDataMap {

    private final Direction collisionDirection;
    private final float inside;

    CollisionDataMap(Direction collisionDirection, float inside){
        this.collisionDirection = collisionDirection;
        this.inside = inside;
    }

    /**
     * Gets the direction.
     * @return the direction
     */
    Direction getCollisionDirection(){
        return collisionDirection;
    }

    /**
     * Gets the overlap distance.
     * @return the overlap distance.
     */
    float getInside(){
        return inside;
    }
}
