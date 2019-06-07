package main.java.level;

import main.java.graphics.TexturedModel;
import main.java.math.Vector2f;
import main.java.math.Vector3f;

/**
 * Extension of Entity that is able to move and be affected by gravity.
 */
public abstract class MovableEntity extends Entity {

    private static final float GRAVITY_CONSTANT = -20.0f;
    private static final float MAX_GRAVITY_ACCELERATION = -20.0f;

    private Vector2f velocity;
    private boolean hasGravity;
    private float gravityAcceleration;

    /**
     * Constructor for all the normal entity things but with the ability to choose if it's affected by gravity.
     * @param position the initial position of this MovableEntity.
     * @param rotation the initial rotation of this MovableEntity.
     * @param scale the initial scale of this MovableEntity.
     * @param hasGravity whether it is affected by gravity.
     * @param texturedModels TexturedModel's that this Entity should contain. Variable amount.
     */
    MovableEntity(Vector3f position, float rotation, float scale, boolean hasGravity, TexturedModel ... texturedModels) {
        super(position, rotation, scale, texturedModels);
        this.velocity = new Vector2f();
        this.hasGravity = hasGravity;
        this.gravityAcceleration = 0.0f;
    }

    /**
     * Motion calculation should be done by individual child classes as it may differ a lot.
     * @param deltaTime the delta time gotten from the timing circuit in Main.
     */
    public abstract void calculateMotion(float deltaTime);

    /**
     * Calculates the motion and updates the main matrix.
     * @param deltaTime the delta time gotten from the timing circuit in Main.
     */
    void update(float deltaTime) {
        resetVelocity();
        calculateMotion(deltaTime);
    }

    /**
     * Calculates gravity for this MovableEntity. Subject to change, refactoring and movement.
     * @param compensation a value with which the gravity is compensated. Used for player jumping. Probably bad implementation.
     * @param deltaTime the delta time gotten from the timing circuit in Main.
     */
    void calculateGravity(float compensation, float deltaTime){
        if (this.hasGravity){
            if (this.gravityAcceleration > MAX_GRAVITY_ACCELERATION - compensation){
                this.gravityAcceleration += GRAVITY_CONSTANT * deltaTime;
            }else{
                this.gravityAcceleration = MAX_GRAVITY_ACCELERATION - compensation;
            }
            this.velocity.addY(this.gravityAcceleration + compensation);
        }
    }

    public void setHasGravity(boolean hasGravity){
        this.hasGravity = hasGravity;
    }

    /**
     * Resets the X and Y velocities to 0.
     */
    void resetVelocity(){
        this.velocity.reset();
    }

    /**
     * Resets the gravity acceleration to 0.
     */
    public void resetGravityAcceleration(){
        this.gravityAcceleration = 0.0f;
    }

    /**
     * Gets the X and Y velocities.
     * @return a Vector2d containing the X and Y velocities.
     */
    Vector2f getVelocity() {
        return this.velocity;
    }

    /**
     * Gets the gravity acceleration.
     * @return the gravity acceleration
     */
    float getGravityAcceleration(){
        return this.gravityAcceleration;
    }
}
