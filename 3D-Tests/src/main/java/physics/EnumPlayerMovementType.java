package main.java.physics;

public enum EnumPlayerMovementType {
    LEFT(-1.0f),
    RIGHT(1.0f),
    STAND(0.0f);

    public final float directionFactor;

    EnumPlayerMovementType(float directionFactor){
        this.directionFactor = directionFactor;
    }
}
