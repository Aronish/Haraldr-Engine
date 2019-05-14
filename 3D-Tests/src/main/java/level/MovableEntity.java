package main.java.level;

import main.java.Camera;
import main.java.debug.Logger;
import main.java.graphics.TexturedModel;
import main.java.math.Vector3f;

//TODO Movable objects such as Player should be a subclass of MovableEntity. This class will hold methods and attributes regarding movement, momentum, gravity,
//TODO velocity and so on. Unification of things that require physics is necessary if I want interfaces to be used like e.g. IHasPhysics.
//TODO Modular physics!
public class MovableEntity extends Entity {

    private double mass;
    private double velocity;
    private double acceleration;
    private double force;

    MovableEntity(Vector3f position, float rotation, float scale, TexturedModel ... texturedModels){
        super(position, rotation, scale, texturedModels);
        this.mass = 1.0d;
    }

    MovableEntity(Vector3f position, float rotation, float scale, double mass, TexturedModel ... texturedModels){
        super(position, rotation, scale, texturedModels);
        this.mass = mass;
        this.force = 0.0d;
        this.acceleration = this.force / this.mass;
    }
    //TODO Continue developing force system. Override if Player specific. E.g. WALK_SPEED
    private void test(double deltaTime){
        if (this.velocity < 0.2d){
            this.velocity += this.acceleration * deltaTime;
        }
        addPosition(new Vector3f((float) this.velocity, 0.0f));
    }

    void update(double deltaTime) {
        test(deltaTime);
        Camera.setPosition(getPosition().multiply(Camera.scale));
        super.updateMatrix();
    }

    public void setVelocity(double velocity){
        this.velocity = velocity;
    }

    public void setForce(double force){
        this.force = force;
        this.acceleration = this.force / this.mass;
        Logger.setInfoLevel();
        Logger.log(this.velocity);
    }

    public void setWalking(double deltaTime, double velocity){
        addPosition(new Vector3f((float) (velocity * deltaTime), 0.0f));
    }

    public void resetMotion(){
        this.force = 0.0d;
        this.acceleration = 0.0d;
        this.velocity = 0.0d;
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
