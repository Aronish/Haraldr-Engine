package main.java.level;

import main.java.Camera;
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
        this.force = 0.05d;
        this.acceleration = this.force / this.mass;
    }
    //TODO Continue developing force system.
    private void test(double deltaTime){
        this.velocity += this.acceleration * deltaTime;
        addPosition(new Vector3f(0.0f, (float) -this.velocity));
        Camera.setPosition(getPosition());
    }

    void updateMatrix(double deltaTime) {
        test(deltaTime);
        super.updateMatrix();
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
