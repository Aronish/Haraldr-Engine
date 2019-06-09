package main.java.level;

import main.java.Camera;
import main.java.debug.Logger;
import main.java.graphics.Models;
import main.java.graphics.Renderer;
import main.java.math.Vector3f;
import main.java.physics.EnumPlayerMovementType;

/**
 * The player that you move around in the world.
 */
public class Player extends MovableEntity {

    private static final float WALK_SPEED = 4.0f;
    private static final float JUMP_STRENGTH = 9.0f;
    private static final float RUN_MULTIPLIER = 1.5f;

    private EnumPlayerMovementType movementType;
    private boolean isJumping;
    private boolean isRunning;
    private boolean isFalling;
    private boolean isStanding;

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
     * @param position the position of this Player. Origion vector to the top left corner of the model.
     * @param rotation the rotation around the z-axis, in degrees. CCW.
     * @param scale the scale multiplier of this Player.
     */
    private Player(Vector3f position, float rotation, float scale){
        super(position, rotation, scale, true, Models.PLAYER);
        this.movementType = EnumPlayerMovementType.STAND;
        this.isJumping = false;
        this.isRunning = false;
        this.isFalling = false;
        this.isStanding = false;
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

    public void setStanding(boolean isStanding){
        this.isStanding = isStanding;
    }

    public boolean isFalling(){
        return this.isFalling;
    }

    /**
     * Calculates the motion from factors like speed, isJumping and isRunning.
     * @param deltaTime the delta time gotten from the timing circuit in Main.
     */
    //TODO THIS IS YOUR LOOP AND PARADOX SATAN: CANNOT BE UNSUMMONED, GO TO HELL!!!!!!!
    @Override
    public void calculateMotion(float deltaTime){
        //---Walking Calculations---\\
        if (this.isRunning){
            getVelocity().addX(WALK_SPEED * RUN_MULTIPLIER * this.movementType.directionFactor);
        }else{
            getVelocity().addX(WALK_SPEED * this.movementType.directionFactor);
        }
        //---Jumping-Calculations---\\
        if (this.isJumping){
            if (Math.abs(getGravityAcceleration()) < JUMP_STRENGTH){
                getVelocity().addY(JUMP_STRENGTH);
                calculateGravity(0.0f, deltaTime);
            } else {
                this.isJumping = false;
            }
        }else if (this.isStanding){
            calculateGravity(0.0f, deltaTime);
            Logger.log("STANDING");
        }else if (this.isFalling){
            if (getVelocity().getY() < 0.0f){
                calculateGravity(JUMP_STRENGTH, deltaTime);
            }else{
                calculateGravity(0.0f, deltaTime);
            }
        }
        addPosition(new Vector3f(this.getVelocity().getX() * deltaTime, this.getVelocity().getY() * deltaTime));
        Camera.setPosition(getPosition().add(getMiddle()));
        this.isStanding = false;
        this.isFalling = true;
    }
}