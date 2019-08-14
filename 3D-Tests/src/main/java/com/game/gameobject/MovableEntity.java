package com.game.gameobject;

import com.game.Camera;
import com.game.math.Vector2f;
import com.game.math.Vector3f;

/**
 * Extension of Entity that is able to move and be affected by gravity.
 */
public abstract class MovableEntity extends Entity {

    private static final float GRAVITY_CONSTANT = -25.0f;
    private static final float MAX_GRAVITY_ACCELERATION = -30.0f;

    protected Vector2f velocity;
    protected float gravityAcceleration;
    private boolean hasGravity;

    MovableEntity(Vector3f position, float rotation, float scale, boolean hasGravity, GameObject gameObjectType) {
        super(position, rotation, scale, gameObjectType);
        this.hasGravity = hasGravity;
        velocity = new Vector2f();
        gravityAcceleration = 0.0f;
    }

    /**
     * Motion calculation should be done by individual child classes as it may differ a lot.
     * @param camera the current camera.
     * @param deltaTime the delta time gotten from the timing circuit in Application.
     */
    public abstract void calculateMotion(Camera camera, float deltaTime);

    /**
     * Resets velocities and calculates the motion from factors like speed, isJumping, isRunning and isBoosting.
     * @param camera the current camera.
     * @param deltaTime the delta time gotten from the timing circuit in Main.
     */
    public void update(Camera camera, float deltaTime) {
        resetVelocity();
        calculateMotion(camera, deltaTime);
    }

    /**
     * Calculates gravity for this MovableEntity.
     * @param compensation a value with which the gravity is compensated. E.g. gravity becomes stronger to account for the jumping velocity.
     * @param deltaTime the delta time gotten from the timing circuit in Application.
     */
    void calculateGravity(float compensation, float deltaTime){
        if (hasGravity){
            if (gravityAcceleration > MAX_GRAVITY_ACCELERATION - compensation){
                gravityAcceleration += GRAVITY_CONSTANT * deltaTime;
            }else{
                gravityAcceleration = MAX_GRAVITY_ACCELERATION - compensation;
            }
            velocity.addY(gravityAcceleration + compensation);
        }
    }

    public void setHasGravity(boolean hasGravity){
        this.hasGravity = hasGravity;
    }

    private void resetVelocity(){
        velocity.reset();
    }

    public void resetGravityAcceleration(){
        gravityAcceleration = 0.0f;
    }
}
