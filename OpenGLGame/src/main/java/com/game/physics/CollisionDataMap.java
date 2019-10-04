package com.game.physics;

/**
 * Simple data pair with direction and overlap distance used for collision detection.
 */
class CollisionDataMap
{
    private final Direction collisionDirection;
    private final float inside;

    CollisionDataMap(Direction collisionDirection, float inside)
    {
        this.collisionDirection = collisionDirection;
        this.inside = inside;
    }

    Direction getCollisionDirection()
    {
        return collisionDirection;
    }

    float getInside()
    {
        return inside;
    }
}
