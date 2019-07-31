package com.game.level;

import com.game.level.gameobject.EnumGameObjects;
import com.game.math.Vector2f;
import com.game.math.Vector3f;

/**
 * Extension of Entity that is able to move and be affected by gravity.
 */
public abstract class MovableEntity extends Entity {

    private static final float GRAVITY_CONSTANT = -25.0f;
    private static final float MAX_GRAVITY_ACCELERATION = -30.0f;

    private Vector2f velocity;
    private boolean hasGravity;
    private float gravityAcceleration;

    /**
     * Constructor for all the normal entity things but with the ability to choose if it's affected by gravity.
     * @param position the initial position of this MovableEntity.
     * @param rotation the initial rotation of this MovableEntity.
     * @param scale the initial scale of this MovableEntity.
     * @param hasGravity whether it is affected by gravity.
     */
    MovableEntity(Vector3f position, float rotation, float scale, boolean hasGravity, EnumGameObjects gameObjectType) {
        super(position, rotation, scale, gameObjectType);
        this.hasGravity = hasGravity;
        velocity = new Vector2f();
        gravityAcceleration = 0.0f;
    }

    /**
     * Motion calculation should be done by individual child classes as it may differ a lot.
     * @param deltaTime the delta time gotten from the timing circuit in Application.
     */
    public abstract void calculateMotion(float deltaTime);

    /**
     * Calculates the motion.
     * @param deltaTime the delta time gotten from the timing circuit in Application.
     */
    public void update(float deltaTime) {
        resetVelocity();
        calculateMotion(deltaTime);
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

    /**
     * @param hasGravity whether this MovableEntity should be affected by gravity.
     */
    public void setHasGravity(boolean hasGravity){
        this.hasGravity = hasGravity;
    }

    /**
     * Resets the X and Y velocities to 0.
     */
    private void resetVelocity(){
        velocity.reset();
    }

    /**
     * Resets the gravity acceleration to 0.
     */
    public void resetGravityAcceleration(){
        gravityAcceleration = 0.0f;
    }

    /**
     * @return a Vector2d containing the X and Y velocities.
     */
    Vector2f getVelocity() {
        return velocity;
    }

    /**
     * @return the gravity acceleration
     */
    float getGravityAcceleration(){
        return gravityAcceleration;
    }
}
