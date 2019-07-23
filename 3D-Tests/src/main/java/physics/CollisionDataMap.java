package main.java.physics;

/**
 * Simple data pair with direction and overlap distance used for collision detection.
 */
class CollisionDataMap {

    private final EnumDirection collisionDirection;
    private final float inside;

    CollisionDataMap(EnumDirection collisionDirection, float inside){
        this.collisionDirection = collisionDirection;
        this.inside = inside;
    }

    /**
     * Gets the direction.
     * @return the direction
     */
    EnumDirection getCollisionDirection(){
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
