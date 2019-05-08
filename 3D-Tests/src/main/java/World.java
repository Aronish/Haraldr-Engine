package main.java;

import main.java.graphics.Models;
import main.java.math.Vector3f;

/**
 * Main world object, on which everything should be located to be inside the world.
 * For future development style, this class should manage everything to do with the world in a level.
 */
class World extends Entity {

    /**
     * Default constructor without parameters.
     */
    World(){
        this(new Vector3f(0.0f, -1.0f), 0.0f, 1.0f);
    }

    World(Vector3f position){
        this(position, 0.0f, 1.0f);
    }

    /**
     * Constructor with parameters for position, rotation and scale.
     * @param position the position of the object. An origin vector. Bottom left corner.
     * @param rotation the rotation around the z-axis, in degrees.
     * @param scale the scale multiplier of this object.
     */
    private World(Vector3f position, float rotation, float scale){
        super(position, rotation, scale, Models.getDIRT_LAYER(), Models.getGRASS_LAYER());
    }
}