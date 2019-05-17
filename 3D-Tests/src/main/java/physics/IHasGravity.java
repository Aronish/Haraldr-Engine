package main.java.physics;

import main.java.level.MovableEntity;

public interface IHasGravity {
    default <T extends MovableEntity> void gravity(T gravObj){

    }
}
