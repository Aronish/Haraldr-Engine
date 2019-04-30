package main.java;
//TODO Fix JavaDoc and matrix locations.
import main.java.graphics.Models;
import main.java.math.Vector3f;

/**
 * The player that is visible in the center of the screen.
 * This is a static model and stays in place.
 */
class Player extends Entity {

    private double velocity;
    private boolean isMoving;

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
    Player(Vector3f position, float rotation, float scale){
        super(position, rotation, scale, Models.PLAYER);
        this.velocity = 5.0d;
        this.isMoving = false;
    }
    /**
     * Calculates the x position based on the velocity and delta time.
     * @param x whether the player should move towards the positive or negative direction. If true, towards positive.
     * @param deltaTime the delta time from the update method in Main.
     */
    void calculateXPosition(boolean x, double deltaTime){
        if(x){
            this.position.x += this.velocity * deltaTime;
        }else{
            this.position.x -= this.velocity * deltaTime;
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
            this.position.y += this.velocity * deltaTime;
        }else{
            this.position.y -= this.velocity * deltaTime;
        }
        this.updateMatrix();
    }

    /**
     * Sets the property isMoving to true. Used in collision detection to avoid calculating collisions
     * when the player isn't moving.
     * @param isMoving whether the player should be set as moving or not.
     */
    void setIsMoving(boolean isMoving){
        this.isMoving = isMoving;
    }

    /**
     * Returns the moving state of the player.
     * @return whether the player is moving or not.
     */
    boolean isMoving(){
        return this.isMoving;
    }

    /**
     * Set the velocity of this player.
     * @param velocity the velocity to set.
     */
    void setVelocity(double velocity){
        this.velocity = velocity;
    }

    @Override
    public void updateMatrix() {
        super.updateMatrix();
        setIsMoving(true);
    }
}