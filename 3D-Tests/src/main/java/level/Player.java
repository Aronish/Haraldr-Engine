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

    private static final float WALK_SPEED = 5.0f;
    private static final float JUMP_STRENGTH = 10.0f;
    private static final float RUN_MULTIPLIER = 1.5f;
    private static final float JUMP_SLOWDOWN = 0.5f;

    private EnumPlayerMovementType movementType;
    private boolean isJumping;
    private boolean isRunning;

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
        super(position, rotation, scale, true, Models.PLAYER);
        this.movementType = EnumPlayerMovementType.STAND;
        this.isJumping = false;
        this.isRunning = false;
    }

    public void setMovementType(EnumPlayerMovementType movementType){
        this.movementType = movementType;
    }

    public void setJumping(boolean isJumping){
        this.isJumping = isJumping;
    }

    public void setRunning(boolean isRunning){
        this.isRunning = isRunning;
    }

    @Override
    public void calculateMotion(float deltaTime) {
        if (this.movementType == EnumPlayerMovementType.STAND && !this.isJumping){
            this.resetVelocity();
        }else{
            if (this.isRunning && this.isJumping){
                this.getVelocity().addX(WALK_SPEED * RUN_MULTIPLIER * JUMP_SLOWDOWN * this.movementType.directionFactor);
            }else if (this.isRunning){
                this.getVelocity().addX(WALK_SPEED * RUN_MULTIPLIER * this.movementType.directionFactor);
            }else if (this.isJumping){
                this.getVelocity().addX(WALK_SPEED * JUMP_SLOWDOWN * this.movementType.directionFactor);
            }else{
                this.getVelocity().addX(WALK_SPEED * this.movementType.directionFactor);
            }
        }
        if (this.isJumping) {
            if (Math.abs(this.getGravityAcceleration()) < JUMP_STRENGTH) {
                this.getVelocity().addY(JUMP_STRENGTH);
                calculateGravity(0.0f, deltaTime);
            } else {
                this.isJumping = false;
            }
        }else{
            calculateGravity(JUMP_STRENGTH, deltaTime);
        }//TODO Make it stop bouncing
        if (this.getVelocity().getY() > 0.0f){
            Renderer.setClearColor(0.2f, 0.6f, 0.65f, 1.0f);
        }else{
            Renderer.setClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        }
        Logger.log(this.getVelocity().getY());
        addPosition(new Vector3f(this.getVelocity().getX() * deltaTime, this.getVelocity().getY() * deltaTime));
        Camera.setPosition(getPosition());
    }
}