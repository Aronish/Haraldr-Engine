package main.java.level;

import main.java.debug.Logger;
import main.java.graphics.TexturedModel;
import main.java.math.Vector2d;
import main.java.math.Vector3f;

public abstract class MovableEntity extends Entity {

    protected double mass;
    protected double velocityX, velocityY;
    protected double accelerationX, accelerationY;
    protected double forceX, forceY;

    MovableEntity(Vector3f position, float rotation, float scale, TexturedModel ... texturedModels){
        this(position, rotation, scale, 1.0d, texturedModels);
    }

    MovableEntity(Vector3f position, float rotation, float scale, double mass, TexturedModel ... texturedModels){
        super(position, rotation, scale, texturedModels);
        this.mass = mass;
        this.velocityX = 0.0d;
        this.velocityY = 0.0d;
        this.accelerationX = 0.0d;
        this.accelerationY = 0.0d;
    }

    public abstract void calculateMotion(double deltaTime);

    void update(double deltaTime) {
        calculateMotion(deltaTime);
        super.updateMatrix();
    }

    public void setVelocity(Vector2d velocities){
        this.velocityX = velocities.getX();
        this.velocityY = velocities.getY();
    }

    public void setForce(double deltaTime){
        setForce(new Vector2d(0.0d, 0.0d), deltaTime);
    }

    public void setForce(Vector2d forces, double deltaTime){
        this.forceX = forces.getX();
        this.forceY = forces.getY();
        this.accelerationX = (this.forceX / this.mass) * deltaTime;
        this.accelerationY = (this.forceY / this.mass) * deltaTime;
        Logger.setInfoLevel();
        Logger.log(this.accelerationX + " " + this.accelerationY);
    }

    /**
     * Gets the width of the player's bounding box.
     * @return the width of the bounding box.
     */
    public float getWidth(){
        return getTexturedModels().get(0).getAABB().getWidth();
    }

    /**
     * Gets the height of the player's bounding box.
     * @return the height of the bounding box.
     */
    public float getHeight(){
        return getTexturedModels().get(0).getAABB().getHeight();
    }

    /**
     * Gets the middle of the player's bounding box.
     * @return the middle of the bounding box.
     */
    public Vector3f getMiddle() {
        return getTexturedModels().get(0).getAABB().getMiddle();
    }
}
