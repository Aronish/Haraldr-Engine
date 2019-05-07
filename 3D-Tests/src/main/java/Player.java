package main.java;

import main.java.graphics.Models;
import main.java.math.Vector3f;

/**
 * The player that is visible in the center of the screen.
 */
class Player extends Entity {

    private double velocity;

    /**
     * Default constructor if no arguments are provided.
     */
    Player(){
        this(new Vector3f(), 0.0f, 1.0f);
    }

    /**
     * Constructor with parameters for position, rotation and scale.
     * @param position the position of the object. An origin vector. Bottom left corner.
     * @param rotation the rotation around the z-axis, in degrees.
     * @param scale the scale multiplier of this object.
     */
    private Player(Vector3f position, float rotation, float scale){
        super(position, rotation, scale, Models.PLAYER);
        this.velocity = 5.0d;
    }
    /**
     * Calculates the x position based on the velocity and delta time.
     * @param x whether the player should move towards the positive or negative direction. If true, towards positive.
     * @param deltaTime the delta time from the update method in Main.
     */
    void calculateXPosition(boolean x, double deltaTime){
        if(x){
            addPosition(new Vector3f((float) (this.velocity * deltaTime), 0.0f));
        }else{
            addPosition(new Vector3f((float) -(this.velocity * deltaTime), 0.0f));
        }
        this.updateMatrix();
    }

    /**
     * Calculates the y position based on the velocity and delta time.
     * @param y whether the player should move towards the positive or negative direction. If true, towards positive.
     * @param deltaTime the delta time from the update method in Main.
     */
    void calculateYPosition(boolean y, double deltaTime){
        if(y){
            addPosition(new Vector3f(0.0f, (float) (this.velocity * deltaTime)));
        }else{
            addPosition(new Vector3f(0.0f, (float) -(this.velocity * deltaTime)));
        }
        this.updateMatrix();
    }

    /**
     * Set the velocity of this player.
     * @param velocity the velocity to set.
     */
    void setVelocity(double velocity){
        this.velocity = velocity;
    }

    float getWidth(){
        return getTexturedModels().get(0).getAABB().getWidth();
    }

    float getHeight(){
        return getTexturedModels().get(0).getAABB().getHeight();
    }

    Vector3f getMiddle() {
        return getTexturedModels().get(0).getAABB().getMiddle();
    }
}