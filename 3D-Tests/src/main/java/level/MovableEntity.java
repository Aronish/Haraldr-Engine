package main.java.level;

import main.java.graphics.TexturedModel;
import main.java.math.Vector2f;
import main.java.math.Vector3f;

public abstract class MovableEntity extends Entity {

    private Vector2f velocity;

    MovableEntity(Vector3f position, float rotation, float scale, TexturedModel... texturedModels) {
        super(position, rotation, scale, texturedModels);
        this.velocity = new Vector2f();
    }

    public abstract void calculateMotion(float deltaTime);

    void update(float deltaTime) {
        calculateMotion(deltaTime);
        super.updateMatrix();
    }

    void setVelocity(){
        this.velocity = new Vector2f();
    }

    public Vector2f getVelocity() {
        return this.velocity;
    }
}
