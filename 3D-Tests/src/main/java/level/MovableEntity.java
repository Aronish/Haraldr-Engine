package main.java.level;

import main.java.graphics.TexturedModel;
import main.java.math.Vector2f;
import main.java.math.Vector3f;

public abstract class MovableEntity extends Entity {

    private Vector2f velocity;
    private boolean hasGravity;
    protected float gravityAcceleration;
    private static final float GRAVITY_CONSTANT = -10.0f;

    MovableEntity(Vector3f position, float rotation, float scale, boolean hasGravity, TexturedModel... texturedModels) {
        super(position, rotation, scale, texturedModels);
        this.velocity = new Vector2f();
        this.hasGravity = hasGravity;
        this.gravityAcceleration = 0.0f;
    }

    public abstract void calculateMotion(float deltaTime);

    void update(float deltaTime) {
        calculateMotion(deltaTime);
        resetVelocity();
        super.updateMatrix();
    }

    void calculateGravity(float deltaTime){
        if (this.hasGravity){
            this.gravityAcceleration += GRAVITY_CONSTANT * deltaTime;
            this.velocity.addY(this.gravityAcceleration);
        }
    }

    void resetVelocity(){
        this.velocity.reset();
    }

    public void resetGravityAcceleration(){
        this.gravityAcceleration = 0.0f;
    }

    public Vector2f getVelocity() {
        return this.velocity;
    }
}
