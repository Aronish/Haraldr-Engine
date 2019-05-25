package main.java.level;

import main.java.Camera;
import main.java.graphics.Models;
import main.java.math.Vector3f;
import main.java.physics.EnumPlayerMovementType;
import main.java.physics.IHasGravity;

/**
 * The player that you move around in the world.
 */
public class Player extends MovableEntity implements IHasGravity {

    private static final float WALK_SPEED = 7.0f;
    private EnumPlayerMovementType movementType;

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
    }

    public void setMovementType(EnumPlayerMovementType movementType){
        this.movementType = movementType;
    }

    @Override
    public void calculateMotion(float deltaTime) {
        if (this.movementType == EnumPlayerMovementType.STAND){
            this.setVelocity();
        }else{
            this.getVelocity().setX(WALK_SPEED * this.movementType.directionFactor);
        }
        gravity();
        addPosition(new Vector3f(this.getVelocity().getX() * deltaTime, this.getVelocity().getY() * deltaTime));
        Camera.setPosition(getPosition());
    }

    @Override
    public void gravity() {
        calculateGravity(this);
    }
}