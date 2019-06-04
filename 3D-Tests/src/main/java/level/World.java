package main.java.level;

import main.java.debug.IHasDebug;
import main.java.debug.Logger;
import main.java.graphics.Models;
import main.java.math.SimplexNoise;
import main.java.math.Vector3f;

import java.util.ArrayList;
import java.util.Random;

/**
 * Main world object, on which everything should be located to be inside the world.
 * For future development style, this class should manage everything to do with the world in a level.
 */
class World extends Entity implements IHasDebug {

    private ArrayList<Obstacle> obstacles;

    /**
     * Constructor with the position of the Player in the same Level as this World.
     */
    World(){
        this(new Vector3f(0.0f, -1.0f), 0.0f, 1.0f);
    }

    /**
     * Constructor with the initial position of this World, as well as the position of the Player in the same Level as this World.
     * @param position the initial position of this World.
     */
    World(Vector3f position){
        this(position, 0.0f, 1.0f);
    }

    /**
     * Constructor with parameters for position, rotation and scale.
     * @param position the position of the object. An origin vector. Bottom left corner.
     * @param rotation the rotation around the z-axis, in degrees.
     * @param scale the scale multiplier of this object.
     */
    private World(Vector3f position, float rotation, float scale){
        super(position, rotation, scale, Models.getDIRT_LAYER(), Models.getGRASS_LAYER());
        this.obstacles = new ArrayList<>();
        generateWorld();
    }

    private void generateWorld(){
        Random random = new Random();
        for (float i = 0; i < 10.0f; i += 0.2f){
            float y = (float) SimplexNoise.noise(random.nextDouble() * 20.0d, random.nextDouble() * 20.0d) + 2;
            Logger.log(y);
            this.obstacles.add(new Obstacle(new Vector3f(i * 5, y)));
        }
    }

    ArrayList<Obstacle> getObstacles(){
        return this.obstacles;
    }
}