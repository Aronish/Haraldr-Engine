package com.game.gameobject;

import com.game.Camera;
import com.game.math.Vector2f;
import com.game.math.Vector3f;
import com.game.physics.PlayerMovementType;

import static com.game.Main.fastFloor;
import static com.game.world.Grid.GRID_SIZE;

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

    public Player(){
        this(new Vector3f(), 0.0f, 1.0f);
    }

    public Player(Vector3f position){
        this(position, 0.0f, 1.0f);
    }

    public Player(Vector3f position, float scale){
        this(position, 0.0f, scale);
    }

    private Player(Vector3f position, float rotation, float scale){
        super(position, rotation, scale, true, GameObject.PLAYER);
        gridPosition = new Vector2f();
        movementType = PlayerMovementType.STAND;
        isJumping = false;
        isRunning = false;
        isFalling = false;
        isBoosting = false;
    }

    public void setMovementType(PlayerMovementType movementType){
        this.movementType = movementType;
    }

    public void setJumping(boolean isJumping){
        this.isJumping = isJumping;
    }

    public void setRunning(boolean isRunning){
        this.isRunning = isRunning;
    }

    public void setFalling(boolean isFalling){
        this.isFalling = isFalling;
    }

    public void setBoosting(boolean isBoosting){
        this.isBoosting = isBoosting;
    }

    public boolean isJumping(){
        return isJumping;
    }

    public boolean isFalling(){
        return isFalling;
    }

    public Vector2f getGridPosition(){
        return gridPosition;
    }

    public void resetPosition(){
        position.set(0.0f, 250.0f);
    }

    @Override
    public void update(Camera camera, float deltaTime) {
        super.update(camera, deltaTime);
        gridPosition.set((float) fastFloor(getPosition().getX() / GRID_SIZE), (float) fastFloor(getPosition().getY() / GRID_SIZE));
    }

    @Override
    public void calculateMotion(Camera camera, float deltaTime){
        ///// WALKING ///////////////////////////
        if (movementType != PlayerMovementType.STAND){
            scale.set(movementType.directionFactor, 1.0f);
        }
        if (isBoosting){
            velocity.addX(WALK_SPEED * BOOST_MULTIPLIER * movementType.directionFactor);
        }else if (isRunning){
            velocity.addX(WALK_SPEED * RUN_MULTIPLIER * movementType.directionFactor);
        }else{
            velocity.addX(WALK_SPEED * movementType.directionFactor);
        }
        ///// JUMPING ///////////////////////////
        if (isJumping) {
            if (Math.abs(gravityAcceleration) < JUMP_STRENGTH) {
                velocity.addY(JUMP_STRENGTH);
                calculateGravity(0.0f, deltaTime);
            } else {
                isJumping = false;
            }
        }else if (isFalling){
            calculateGravity(JUMP_STRENGTH, deltaTime);
        }else{
            calculateGravity(0.0f, deltaTime);
        }
        addPosition(new Vector3f(velocity.getX() * deltaTime, velocity.getY() * deltaTime));
        camera.setPosition(getPosition().add(getGameObjectType().model.getAABB().getMiddle()));
    }
}