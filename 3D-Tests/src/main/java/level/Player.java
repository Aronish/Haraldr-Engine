package main.java.level;

import main.java.graphics.Models;
import main.java.math.Vector3f;

/**
 * The player that you move around in the world.
 */
public class Player extends Entity {

    private double velocityX, velocityY;

    /**
     * Default constructor if no arguments are provided.
     */
    Player(){
        this(new Vector3f(), 0.0f, 1.0f);
    }

    /**
     * Constructor with parameters for position, rotation and scale.
     * @param position the position of the player. Origion vector to the top left corner of the model.
     * @param rotation the rotation around the z-axis, in degrees. CW.
     * @param scale the scale multiplier of this object.
     */
    private Player(Vector3f position, float rotation, float scale){
        super(position, rotation, scale, Models.PLAYER);
        this.velocityX = 5.0d;
        this.velocityY = 5.0d;
    }
    /**
     * Calculates the x position based on the velocity and delta time.
     * @param x whether the player should move towards the positive or negative direction. If true, towards positive.
     * @param deltaTime the delta time from Main#update.
     */
    public void calculateXPosition(boolean x, double deltaTime){
        if(x){
            addPosition(new Vector3f((float) (this.velocityX * deltaTime), 0.0f));
        }else{
            addPosition(new Vector3f((float) -(this.velocityX * deltaTime), 0.0f));
        }
        this.updateMatrix();
    }

    /**
     * Calculates the y position based on the velocity and delta time.
     * @param y whether the player should move towards the positive or negative direction. If true, towards positive.
     * @param deltaTime the delta time from Main#update.
     */
    public void calculateYPosition(boolean y, double deltaTime){
        if(y){
            addPosition(new Vector3f(0.0f, (float) (this.velocityY * deltaTime)));
        }else{
            addPosition(new Vector3f(0.0f, (float) -(this.velocityY * deltaTime)));
        }
        this.updateMatrix();
    }

    /**
     * Set the velocity of this player.
     */
    public void setVelocity(double velocityX, double velocityY){
        this.velocityX = velocityX;
        this.velocityY = velocityY;
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