package com.game.physics;

import java.io.Serializable;

public enum PlayerMovementType implements Serializable {
    LEFT(-1.0f),
    RIGHT(1.0f),
    STAND(0.0f);

    public final float directionFactor;

    PlayerMovementType(float directionFactor){
        this.directionFactor = directionFactor;
    }
}
