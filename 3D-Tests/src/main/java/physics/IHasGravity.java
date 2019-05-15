package main.java.physics;

import main.java.level.MovableEntity;
import main.java.math.Vector2d;

public interface IHasGravity {
    default <T extends MovableEntity> void gravity(T gravObj){

    }
}
