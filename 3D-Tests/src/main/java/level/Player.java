package main.java.level;

import main.java.debug.Logger;
import main.java.graphics.Models;
import main.java.math.Vector3f;

/**
 * The player that you move around in the world.
 */
public class Player extends Entity {

    private double speedX, speedY;
    private double velocityX, velocityY; //This stores the velocity like an accumulator. If there is velocity, it should move.

    private final double JUMP_SPEED_X = 3.0d;
    private final double JUMP_SPEED_Y = 1.0d;

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
        this.speedX = 5.0d;
        this.speedY = 5.0d;
        this.velocityX = 0.0d;
        this.velocityY = 0.0d;
    }

    /**
     * Calculates the x position based on the velocity and delta time.
     * @param x whether the player should move towards the positive or negative direction. If true, towards positive.
     * @param deltaTime the delta time from Main#update.
     */
    public void calculateXPosition(boolean x, double deltaTime){
        if(x){
            addPosition(new Vector3f((float) (this.speedX * deltaTime), 0.0f));
        }else{
            addPosition(new Vector3f((float) -(this.speedX * deltaTime), 0.0f));
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
            addPosition(new Vector3f(0.0f, (float) (this.speedY * deltaTime)));
        }else{
            addPosition(new Vector3f(0.0f, (float) -(this.speedY * deltaTime)));
        }
        this.updateMatrix();
    }

    private void calculateMovement(double deltaTime){
        if (this.velocityX > 1.0d){
            addPosition(new Vector3f((float) (this.velocityX * deltaTime), 0.0f));
            this.velocityX -= this.velocityX * deltaTime * this.JUMP_SPEED_X;
            Logger.setInfoLevel();
            Logger.log(this.velocityX);
        }else{
            this.velocityX = 0.0d;
        }
        if (this.velocityY > 1.0d){
            addPosition(new Vector3f(0.0f, (float) (this.velocityY * deltaTime)));
            this.velocityY -= this.velocityY * deltaTime * this.JUMP_SPEED_Y;
        }else{
            this.velocityY = 0.0d;
        }
    }

    void updateMatrix(double deltaTime) {
        super.updateMatrix();
        calculateMovement(deltaTime);
    }

    /**
     * Set the speed of this player.
     */
    public void setSpeed(double speedX, double speedY){
        this.speedX = speedX;
        this.speedY = speedY;
    }

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