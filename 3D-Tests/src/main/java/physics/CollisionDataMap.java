package main.java.physics;

/**
 * Simple data pair with direction and overlap distance used for collision detection.
 */
class CollisionDataMap {

    private final EnumDirection collisionDirection;
    private final float inside;

    /**
     * Sets default data. Unnecessary.
     */
    CollisionDataMap(EnumDirection direction, float inside){
        this.collisionDirection = direction;
        this.inside = inside;
    }

    /**
     * Gets the direction.
     * @return the direction
     */
    EnumDirection getCollisionDirection(){
        return this.collisionDirection;
    }

    /**
     * Gets the overlap distance.
     * @return the overlap distance.
     */
    float getInside(){
        return this.inside;
    }
}
