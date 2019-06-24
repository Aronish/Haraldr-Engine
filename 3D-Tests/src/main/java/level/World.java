package main.java.level;

import main.java.graphics.Models;
import main.java.math.SimplexNoise;
import main.java.math.Vector2f;
import main.java.math.Vector3f;

import java.util.ArrayList;
import java.util.Random;

/**
 * Main world object, on which everything should be located to be inside the world.
 * For future development style, this class should manage everything to do with the world in a level.
 */
public class World extends Entity {

    private static final Random RANDOM = new Random();

    private static double noiseScale = 0.042d;
    private Grid grid;
    private ArrayList<Entity> tiles;

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
        this.grid = new Grid();
        generateWorld();
    }

    /**
     * Generates a list of WorldTiles with varying heights based on SimplexNoise with a RANDOM seed.
     */
    private void generateWorld(){
        double seed = RANDOM.nextDouble() * 100000.0d;
        for (int i = 0; i < 1000; ++i){
            int y = (int) ((SimplexNoise.noise(i * noiseScale, 0.0d, seed) + 1.0d) / 2.0d * 60.0d);
            fillColumn(new Vector3f(i, y + 20));
        }
        this.grid.populateGrid(this.tiles);
    }

    private void fillColumn(Vector3f position){
        if (position.getY() < 58.0f && RANDOM.nextBoolean()){
            this.tiles.add(new Tree(position.add(new Vector3f(0.0f, 3.0f))));
        }
        Tile topTile = new Tile(position, position.getY() > 55.0f ? Models.SNOW_TILE : Models.GRASS_TILE);
        if (RANDOM.nextBoolean()){
            topTile.setScale(new Vector2f(-1.0f, 1.0f));
        }
        this.tiles.add(topTile);
        {
            int counter = 0;
            for (float i = position.subtractY(1.0f).getY(); i >= 0.0f; --i, ++counter) {
                this.tiles.add(new Tile(new Vector3f(position.getX(), i), i < 40.0f ? (counter > 18 ? Models.STONE_TILE : Models.DIRT_TILE) : (counter > 20 ? Models.STONE_TILE : Models.DIRT_TILE)));
            }
        }
    }

    /**
     * Clears the world list an generates a new one.
     */
    public void regenerateWorld(){
        this.tiles.clear();
        this.grid.clear();
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

    Grid getGrid(){
        return this.grid;
    }

    /**
     * Gets the list of tiles.
     * @return the list of tiles.
     */
    ArrayList<Entity> getTiles(){
        return this.tiles;
    }

    @Override
    void cleanUp() {
        super.cleanUp();
        this.tiles.forEach(Entity::cleanUp);
    }
}