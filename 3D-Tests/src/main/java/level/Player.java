package main.java.level;

import main.java.graphics.Models;
import main.java.math.Vector3f;

/**
 * The player that you move around in the world.
 */
public class Player extends MovableEntity {

    /**
     * Default constructor if no arguments are provided.
     */
    Player(){
        this(new Vector3f(), 0.0f, 1.0f);
    }

    /**
     * Constructor with the initial position.
     * @param position the initial position of this Player.
     */
    Player(Vector3f position){
        this(position, 0.0f, 1.0f);
    }

    /**
     * Constructor with parameters for position, rotation and scale.
     * @param position the position of this Player. Origion vector to the top left corner of the model.
     * @param rotation the rotation around the z-axis, in degrees. CCW.
     * @param scale the scale multiplier of this Player.
     */
    private Player(Vector3f position, float rotation, float scale){
        super(position, rotation, scale, 5.0d, Models.PLAYER);
    }
}