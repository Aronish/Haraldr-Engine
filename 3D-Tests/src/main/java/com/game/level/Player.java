package com.game.level;

import com.game.Camera;
import com.game.level.gameobject.GameObject;
import com.game.math.Vector2f;
import com.game.math.Vector3f;
import com.game.physics.PlayerMovementType;

import static com.game.Main.fastFloor;
import static com.game.level.Grid.GRID_SIZE;

/**
 * The player that you move around in the world.
 */
public class Player extends MovableEntity {

    private static final float WALK_SPEED = 4.0f;
    private static final float JUMP_STRENGTH = 13.0f;
    private static final float RUN_MULTIPLIER = 1.5f;
    private static final float BOOST_MULTIPLIER = 10.0f;

    private Vector2f gridPosition;
    private PlayerMovementType movementType;
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

    /**
     * Constructor with the initial position and scale.
     * @param position the initial position of this Player.
     * @param scale the initial scale of this Player.
     */
    Player(Vector3f position, float scale){
        this(position, 0.0f, scale);
    }

    /**
     * Constructor with parameters for position, rotation and scale.
     * @param position the initial position of this Player.
     * @param rotation the initial rotation around the z-axis, in degrees. CCW.
     * @param scale the initial scale multiplier of this Player.
     */
    private Player(Vector3f position, float rotation, float scale){
        super(position, rotation, scale, true, GameObject.PLAYER);
        gridPosition = new Vector2f();
        movementType = PlayerMovementType.STAND;
        isJumping = false;
        isRunning = false;
        isFalling = false;
        isBoosting = false;
    }

    /**
     * Sets the movement type.
     * @param movementType one of the available movement types for the Player.
     */
    public void setMovementType(PlayerMovementType movementType){
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
     * @return whether this Player is jumping.
     */
    public boolean isJumping(){
        return isJumping;
    }

    /**
     * @return whether this Player is falling.
     */
    public boolean isFalling(){
        return isFalling;
    }

    /**
     * @return the grid coordinates of this Player.
     */
    Vector2f getGridPosition(){
        return gridPosition;
    }

    /**
     * Resets the position to some coordinate.
     */
    public void resetPosition(){
        setPosition(new Vector3f(0.0f, 250.0f));
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        gridPosition.set((float) fastFloor(getPosition().getX() / GRID_SIZE), (float) fastFloor(getPosition().getY() / GRID_SIZE));
    }

    /**
     * Calculates the motion from factors like speed, isJumping, isRunning and isBoosting.
     * @param deltaTime the delta time gotten from the timing circuit in Main.
     */
    @Override
    public void calculateMotion(float deltaTime){
        ///// WALKING ///////////////////////////
        if (movementType != PlayerMovementType.STAND){
            setScale(new Vector2f(movementType.directionFactor, 1.0f));
        }
        if (isBoosting){
            getVelocity().addX(WALK_SPEED * BOOST_MULTIPLIER * movementType.directionFactor);
        }else if (isRunning){
            getVelocity().addX(WALK_SPEED * RUN_MULTIPLIER * movementType.directionFactor);
        }else{
            getVelocity().addX(WALK_SPEED * movementType.directionFactor);
        }
        ///// JUMPING ///////////////////////////
        if (isJumping) {
            if (Math.abs(getGravityAcceleration()) < JUMP_STRENGTH) {
                getVelocity().addY(JUMP_STRENGTH);
                calculateGravity(0.0f, deltaTime);
            } else {
                isJumping = false;
            }
        }else if (isFalling){
            calculateGravity(JUMP_STRENGTH, deltaTime);
        }else{
            calculateGravity(0.0f, deltaTime);
        }
        addPosition(new Vector3f(getVelocity().getX() * deltaTime, getVelocity().getY() * deltaTime));
        Camera.setPosition(getPosition().add(getGameObjectType().model.getAABB().getMiddle()));
    }
}