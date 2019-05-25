package main.java.level;

import main.java.graphics.TexturedModel;
import main.java.math.Vector2d;
import main.java.math.Vector3f;

public abstract class MovableEntity extends Entity {

    private Vector2d velocity;

    MovableEntity(Vector3f position, float rotation, float scale, TexturedModel... texturedModels) {
        super(position, rotation, scale, texturedModels);
        this.velocity = new Vector2d();
    }

    public abstract void calculateMotion(double deltaTime);

    void update(double deltaTime) {
        //Set forces before motion calculation
        calculateMotion(deltaTime); //Calculate motion
        //resetForces(); //Reset forces
        super.updateMatrix();
    }

    public void setVelocity(Vector2d velocity) {
        this.velocity = velocity;
    }

    public Vector2d getVelocity() {
        return this.velocity;
    }
}
