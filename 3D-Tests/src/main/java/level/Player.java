package main.java.level;

import main.java.Camera;
import main.java.graphics.Models;
import main.java.math.Vector2f;
import main.java.math.Vector3f;
import main.java.physics.EnumPlayerMovementType;

import static main.java.Main.fastFloor;
import static main.java.level.Grid.GRID_SIZE;

/**
 * The player that you move around in the world.
 */
public class Player extends MovableEntity {

    private static final float WALK_SPEED = 4.0f;
    private static final float JUMP_STRENGTH = 13.0f;
    private static final float RUN_MULTIPLIER = 1.5f;
    private static final float BOOST_MULTIPLIER = 10.0f;

    private Vector2f gridPosition;
    private EnumPlayerMovementType movementType;
    private boolean isJumping;
    private boolean isRunning;
    private boolean isFalling;
    private boolean isBoosting;

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

    Player(Vector3f position, float scale){
        this(position, 0.0f, scale);
    }

    /**
     * Constructor with parameters for position, rotation and scale.
     * @param position the position of this Player. Origin vector to the top left corner of the model.
     * @param rotation the rotation around the z-axis, in degrees. CCW.
     * @param scale the scale multiplier of this Player.
     */
    private Player(Vector3f position, float rotation, float scale){
        super(position, rotation, scale, true, Models.getPLAYER());
        this.gridPosition = new Vector2f();
        this.movementType = EnumPlayerMovementType.STAND;
        this.isJumping = false;
        this.isRunning = false;
        this.isFalling = false;
        this.isBoosting = false;
    }

    /**
     * Sets the movement type.
     * @param movementType one of the available movement types for the Player.
     */
    public void setMovementType(EnumPlayerMovementType movementType){
        this.movementType = movementType;
    }

    /**
     * Set whether this Player is jumping.
     * @param isJumping whether the Player should be jumping.
     */
    public void setJumping(boolean isJumping){
        this.isJumping = isJumping;
    }

    /**
     * Set whether this Player is running.
     * @param isRunning whether the Player should be running.
     */
    public void setRunning(boolean isRunning){
        this.isRunning = isRunning;
    }

    /**
     * Set whether this Player is falling.
     * @param isFalling whether the Player should be falling.
     */
    public void setFalling(boolean isFalling){
        this.isFalling = isFalling;
    }

    /**
     * Set whether this Player is boosting.
     * @param isBoosting whether the Player should be boosting.
     */
    public void setBoosting(boolean isBoosting){
        this.isBoosting = isBoosting;
    }

    /**
     * Gets whether this Player is jumping.
     * @return whether this Player is jumping.
     */
    public boolean isJumping(){
        return this.isJumping;
    }

    /**
     * Gets whether this Player is falling.
     * @return whether this Player is falling.
     */
    public boolean isFalling(){
        return this.isFalling;
    }

    /**
     * @return the grid coordinates of this Player.
     */
    Vector2f getGridPosition(){
        return this.gridPosition;
    }

    /**
     * Resets the position to some coordinate.
     */
    public void resetPosition(){
        setPosition(new Vector3f(0.0f, 100.0f));
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        this.gridPosition.set((float) fastFloor(this.getPosition().getX() / GRID_SIZE), (float) fastFloor(this.getPosition().getY() / GRID_SIZE));
    }

    /**
     * Calculates the motion from factors like speed, isJumping, isRunning and isBoosting.
     * @param deltaTime the delta time gotten from the timing circuit in Main.
     */
    @Override
    public void calculateMotion(float deltaTime){
        //---Walking Calculations---\\
        if (this.movementType != EnumPlayerMovementType.STAND){
            setScale(new Vector2f(-this.movementType.directionFactor, 1.0f));
        }
        if (this.isBoosting){
            getVelocity().addX(WALK_SPEED * BOOST_MULTIPLIER * this.movementType.directionFactor);
        }else if (this.isRunning){
            getVelocity().addX(WALK_SPEED * RUN_MULTIPLIER * this.movementType.directionFactor);
        }else{
            getVelocity().addX(WALK_SPEED * this.movementType.directionFactor);
        }
        //---Jumping-Calculations---\\
        if (this.isJumping) {
            if (Math.abs(getGravityAcceleration()) < JUMP_STRENGTH) {
                getVelocity().addY(JUMP_STRENGTH);
                calculateGravity(0.0f, deltaTime);
            } else {
                this.isJumping = false;
            }
        }else if (this.isFalling){
            calculateGravity(JUMP_STRENGTH, deltaTime);
        }else{
            calculateGravity(0.0f, deltaTime);
        }
        addPosition(new Vector3f(this.getVelocity().getX() * deltaTime, this.getVelocity().getY() * deltaTime));
        Camera.setPosition(getPosition().add(getMiddle()));
    }
}