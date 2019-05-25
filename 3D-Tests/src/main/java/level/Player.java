package main.java.level;

import main.java.Camera;
import main.java.debug.Logger;
import main.java.graphics.Models;
import main.java.math.Vector2d;
import main.java.math.Vector3f;
import main.java.physics.EnumPlayerMovementType;
import main.java.physics.IHasGravity;

/**
 * The player that you move around in the world.
 */
public class Player extends MovableEntity {

    private static final double WALK_SPEED = 7.0d;
    private EnumPlayerMovementType movementType;
    private EnumPlayerMovementType lastMovementType;

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
        super(position, rotation, scale, Models.PLAYER);
        this.movementType = EnumPlayerMovementType.STAND;
        this.lastMovementType = EnumPlayerMovementType.STAND;
    }

    @Override
    public void calculateMotion(double deltaTime) {
        if (this.movementType != EnumPlayerMovementType.STAND && Math.abs(this.getVelocity().getX()) < WALK_SPEED) {
        }else if (this.lastMovementType == EnumPlayerMovementType.LEFT){
            if (this.getVelocity().getX() < 0.0d){
                //TODO Fix walking and change over to only velocities.
            }else{
                this.movementType = EnumPlayerMovementType.STAND;
            }
        }else if (this.lastMovementType == EnumPlayerMovementType.RIGHT){
            if (this.getVelocity().getX() > 0.0d){

            }else{
                this.movementType = EnumPlayerMovementType.STAND;
            }
        }
        addPosition(new Vector3f((float) (this.getVelocity().getX() * deltaTime), 0.0f));
        Camera.setPosition(getPosition().multiply(Camera.scale));
    }
}