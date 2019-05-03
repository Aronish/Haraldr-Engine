package main.java;

import main.java.graphics.Models;
import main.java.math.Vector3f;

/**
 * Simple 1x1 square Entity.
 */
class Obstacle extends Entity {

    /**
     * Default constructor without parameters.
     */
    Obstacle(){
        this(new Vector3f(), 0.0f, 1.0f);
    }

    /**
     * Constructor with just the position.
     * @param position the initial position of this object.
     */
    Obstacle(Vector3f position){
        this(position, 0.0f, 1.0f);
    }

    /**
     * Constructor with position, rotation and scale parameters.
     * @param position the initial position of this object.
     * @param rotation the initial rotation of this object.
     * @param scale the initial scale of this object.
     */
    private Obstacle(Vector3f position, float rotation, float scale){
        super(position, rotation, scale, Models.OBSTACLE);
    }
}
