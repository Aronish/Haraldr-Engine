package main.java.level;

import main.java.math.SimplexNoise;
import main.java.math.Vector3f;

import java.util.ArrayList;
import java.util.Random;

/**
 * Main world object, on which everything should be located to be inside the world.
 * For future development style, this class should manage everything to do with the world in a level.
 */
public class World extends Entity {

    private static double noiseScale = 0.042d;
    private ArrayList<WorldTile> worldTiles;
    private static Random random;

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
        random = new Random();
        this.worldTiles = new ArrayList<>();
        generateWorld();
    }

    /**
     * Generates a list of WorldTiles with varying heights based on SimplexNoise with a random seed.
     */
    private void generateWorld(){
        double seed = random.nextDouble() * 100000.0d;
        for (int i = 0; i < 1500; ++i){
            int y = (int) ((SimplexNoise.noise(i * noiseScale, 0.0d, seed) + 1.0d) / 2.0d * 60.0d);
            this.worldTiles.add(new WorldTile(new Vector3f(i - 750, y + 20)));
        }
    }

    /**
     * Clears the world list an generates a new one.
     */
    public void regenerateWorld(){
        this.worldTiles.clear();
        generateWorld();
    }

    /**
     * Increases/decreases the noise scale with the provided value.
     * @param dScale how much to change the noise scale.
     */
    public void increaseNoiseScale(double dScale){
        noiseScale += dScale;
    }

    public void resetNoiseScale(){
        noiseScale = 0.042d;
    }

    /**
     * Gets the list of WorldTile'.
     * @return the list of WorldTile's.
     */
    ArrayList<WorldTile> getWorldTiles(){
        return this.worldTiles;
    }

    @Override
    void cleanUp() {
        super.cleanUp();
        this.worldTiles.forEach(WorldTile::cleanUp);
    }
}