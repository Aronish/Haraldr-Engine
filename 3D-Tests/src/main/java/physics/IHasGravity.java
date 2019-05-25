package main.java.physics;

import main.java.level.MovableEntity;

public interface IHasGravity {

    float gravityConstant = -5.0f;

    default <T extends MovableEntity> void calculateGravity(T gravityObject){
        gravityObject.getVelocity().setY(gravityConstant);
    }

    void gravity();
}
