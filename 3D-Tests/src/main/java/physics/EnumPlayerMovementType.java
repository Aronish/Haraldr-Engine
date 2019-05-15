package main.java.physics;

public enum EnumPlayerMovementType {
    LEFT(-1.0d),
    RIGHT(1.0d),
    STAND(0.0d);

    public final double directionFactor;

    EnumPlayerMovementType(double directionFactor){
        this.directionFactor = directionFactor;
    }
}
