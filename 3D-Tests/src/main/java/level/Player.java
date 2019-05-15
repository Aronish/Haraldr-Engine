package main.java.level;

import main.java.debug.Logger;
import main.java.graphics.Models;
import main.java.math.Vector3f;

/**
 * The player that you move around in the world.
 */
public class Player extends MovableEntity {

    private static final double WALK_SPEED = 5.0d;

    /**
     * Default constructor if no arguments are provided.
     */
    Player(){
        this(new Vector3f(), 0.0f, 1.0f);
    }

    /**
     * Constructor with the initial position.
     * @param position the initial position of this Player.
     */
    Player(Vector3f position){
        this(position, 0.0f, 1.0f);
    }

    /**
     * Constructor with parameters for position, rotation and scale.
     * @param position the position of this Player. Origion vector to the top left corner of the model.
     * @param rotation the rotation around the z-axis, in degrees. CCW.
     * @param scale the scale multiplier of this Player.
     */
    private Player(Vector3f position, float rotation, float scale){
        super(position, rotation, scale, 1.0d, Models.PLAYER);
    }

    @Override
    public void calculateMotion(double deltaTime) {
        if (this.forceX != 0.0d){
            this.velocityX += this.accelerationX;
        }else{
            this.velocityX -= this.accelerationX;
            Logger.setInfoLevel();
            Logger.log("Decreases" + this.accelerationX);
        }
        addPosition(new Vector3f((float) (this.velocityX * deltaTime), 0.0f));
    }
}
/*
private void test(double deltaTime){
    Logger.setInfoLevel();
    Logger.log(this.velocity);
    if (this.velocity < 5.0d && this.force != 0.0d) {
        this.velocity += this.acceleration;
    }else if(this.velocity > 0.0d && this.force == 0.0d){
        this.velocity -= this.acceleration;
    }else if(this.velocity < 0.0d){
        this.velocity = 0.0d;
    }
    addPosition(new Vector3f((float) (this.velocity * deltaTime), 0.0f));
}*/