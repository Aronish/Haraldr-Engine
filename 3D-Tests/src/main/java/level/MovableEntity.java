package main.java.level;

import main.java.graphics.TexturedModel;
import main.java.math.Vector2d;
import main.java.math.Vector3f;

public abstract class MovableEntity extends Entity {

    private double mass;
    private Vector2d force;
    private Vector2d velocity;
    private Vector2d acceleration;

    MovableEntity(Vector3f position, float rotation, float scale, TexturedModel ... texturedModels){
        this(position, rotation, scale, 1.0d, texturedModels);
    }

    MovableEntity(Vector3f position, float rotation, float scale, double mass, TexturedModel ... texturedModels){
        super(position, rotation, scale, texturedModels);
        this.mass = mass;
        this.force = new Vector2d();
        this.velocity = new Vector2d();
        this.acceleration = new Vector2d();
    }

    public abstract void calculateMotion(double deltaTime);

    void update(double deltaTime){
        //Set forces before motion calculation
        calculateMotion(deltaTime); //Calculate motion
        resetForces(); //Reset forces
        super.updateMatrix();
    }

    public void setVelocity(Vector2d velocity){
        this.velocity = velocity;
    }

    public void resetForces(){
        this.force.setX();
        this.force.setY();
    }

    public void setForces(Vector2d force, double deltaTime){
        this.force = force;
        this.acceleration.setX((this.force.getX() / this.mass) * deltaTime);
        this.acceleration.setY((this.force.getY() / this.mass) * deltaTime);
    }

    public void resetMotionX(){
        this.force.setX();
        this.velocity.setX();
        this.acceleration.setX();
    }

    public void resetMotionY(){
        this.force.setY();
        this.velocity.setY();
        this.acceleration.setY();
    }

    public Vector2d getForce() {
        return this.force;
    }

    public Vector2d getVelocity(){
        return this.velocity;
    }

    public Vector2d getAcceleration() {
        return this.acceleration;
    }
}
