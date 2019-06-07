package main.java.level;

import main.java.debug.Logger;
import main.java.math.SimplexNoise;
import main.java.math.Vector3f;

import java.util.ArrayList;
import java.util.Random;

/**
 * Main world object, on which everything should be located to be inside the world.
 * For future development style, this class should manage everything to do with the world in a level.
 */
public class World extends Entity {

    private static double noiseScale = 0.05d;

    private ArrayList<WorldTile> tiles;

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
        super(position, rotation, scale);
        this.tiles = new ArrayList<>();
        generateWorld();
    }

    private void generateWorld(){
        Random random = new Random();
        double seed = random.nextDouble() * 10000.0d;
        for (int i = 0; i < 100; ++i){
            int y = (int) ((SimplexNoise.noise(i * noiseScale, 0.0d, seed) + 1.0d) / 2.0d * 20.0d);
            this.tiles.add(new WorldTile(new Vector3f(i - 50, y + 10)));
        }
    }

    public void regenerateWorld(){
        this.tiles = new ArrayList<>();
        generateWorld();
    }

    public void increaseNoiseScale(double dScale){
        Logger.log(noiseScale);
        noiseScale += dScale;
    }

    ArrayList<WorldTile> getTiles(){
        return this.tiles;
    }
}