package main.java.level;

import main.java.graphics.Models;
import main.java.graphics.TexturedModel;
import main.java.math.Vector3f;

/**
 * Simple 1x1 square Entity.
 */
public class Obstacle extends Entity {

    /**
     * Default constructor without parameters.
     */
    Obstacle(){
        this(new Vector3f(), 0.0f, 1.0f, Models.OBSTACLE);
    }

    /**
     * Constructor with just the position.
     * @param position the initial position of this object.
     */
    Obstacle(Vector3f position){
        this(position, 0.0f, 1.0f, Models.OBSTACLE);
    }

    Obstacle(Vector3f position, TexturedModel... texturedModels){
        this(position, 0.0f, 1.0f, texturedModels);
    }

    /**
     * Constructor with position, rotation and scale parameters.
     * @param position the initial position of this object.
     * @param rotation the initial rotation of this object.
     * @param scale the initial scale of this object.
     */
    private Obstacle(Vector3f position, float rotation, float scale){
        this(position, rotation, scale, Models.OBSTACLE);
    }

    private Obstacle(Vector3f position, float rotation, float scale, TexturedModel... texturedModels){
        super(position, rotation, scale, texturedModels);
    }
}
