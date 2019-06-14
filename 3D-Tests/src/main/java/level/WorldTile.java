package main.java.level;

import main.java.graphics.Models;
import main.java.math.Vector2f;
import main.java.math.Vector3f;
import main.java.physics.AABB;

import java.util.ArrayList;
import java.util.Random;

/**
 * A one unit wide pillar of Tile's. The height of these are calculated in World#generateWorld. World also holds a list of these.
 */
public class WorldTile extends Entity {

    private static Random random;
    private ArrayList<Entity> tiles;
    private AABB aabb;

    static {
        random = new Random();
    }

    /**
     * Constructor with just the position.
     * @param position the initial position of this WorldTile.
     */
    WorldTile(Vector3f position){
        this(position, 0.0f, 1.0f);
    }

    /**
     * Constructor with the position, rotation and scale.
     * @param position the position of this WorldTile.
     * @param rotation the rotation around the z-axis, in degrees. CCW.
     * @param scale the scale multiplier of this WorldTile.
     */
    private WorldTile(Vector3f position, float rotation, float scale) {
        super(position, rotation, scale);
        this.tiles = new ArrayList<>();
        if (position.y < 58.0f && random.nextBoolean()){
            this.tiles.add(new Tree(position.add(new Vector3f(0.0f, 3.0f))));
        }
        Tile topTile = new Tile(position, position.y > 55.0f ? Models.SNOW_TILE : Models.GRASS_TILE);
        if (random.nextBoolean()){
            topTile.setScale(new Vector2f(-1.0f, 1.0f));
        }
        this.tiles.add(topTile);
        int counter = 0;
        for (float i = position.subtractY(1.0f).y; i >= 0.0f; --i, ++counter){
            this.tiles.add(new Tile(new Vector3f(position.x, i), i < 40.0f ? (counter > 18 ? Models.STONE_TILE : Models.DIRT_TILE) : (counter > 20 ? Models.STONE_TILE : Models.DIRT_TILE)));
        }
        this.aabb = new AABB(1.0f, position.y);
    }

    /**
     * Gets the list of Tile's.
     * @return the list of Tile's.
     */
    ArrayList<Entity> getTiles(){
        return this.tiles;
    }

    @Override
    void updateMatrix() {
        super.updateMatrix();
        this.tiles.forEach(Entity::updateMatrix);
    }

    @Override
    public float getWidth() {
        return this.aabb.getWidth();
    }

    @Override
    public float getHeight() {
        return this.aabb.getHeight();
    }

    @Override
    void cleanUp() {
        super.cleanUp();
        this.tiles.forEach(Entity::cleanUp);
    }
}