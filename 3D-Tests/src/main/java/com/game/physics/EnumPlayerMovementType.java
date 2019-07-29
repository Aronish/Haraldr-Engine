package com.game.physics;

/**
 * The valid states of movement for the Player. Contains a direction factor to easily change direction of movement.
 */
public enum EnumPlayerMovementType {
    LEFT(-1.0f),
    RIGHT(1.0f),
    STAND(0.0f);

    public final float directionFactor;

    EnumPlayerMovementType(float directionFactor){
        this.directionFactor = directionFactor;
    }
}
