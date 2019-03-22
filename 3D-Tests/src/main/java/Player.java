package main.java;

import main.java.graphics.TexturedModel;
import main.java.math.Vector3f;

/**
 * The player that is visible in the center of the screen.
 * This is a static model and stays in place.
 */
class Player extends TexturedModel {

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
    Player(Vector3f position, float rotation, float scale){
        super(position, rotation, scale);
        this.velocity = 5.0d;
        this.setVertexArray();
        this.setShader("src/main/java/shaders/player_shader");
        this.setTexture("src/main/resources/black.png");
        this.setMatrixLocation();
    }
    /**
     * Calculates the x position based on the velocity and delta time.
     * @param x whether the player should move towards the positive or negative direction. If true, towards positive.
     * @param deltaTime the delta time from the update method in Main.
     */
    public void calculateXPosition(boolean x, double deltaTime){
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
    public void calculateYPosition(boolean y, double deltaTime){
        if(y){
            this.position.y += this.velocity * deltaTime;
        }else{
            this.position.y -= this.velocity * deltaTime;
        }
        this.updateMatrix();
    }

    /**
     * Set the velocity of this player.
     * @param velocity the velocity to set.
     */
    public void setVelocity(double velocity){
        this.velocity = velocity;
    }
}