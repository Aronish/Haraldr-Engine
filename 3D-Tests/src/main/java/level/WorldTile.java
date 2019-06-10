package main.java.level;

import main.java.graphics.Models;
import main.java.math.Vector3f;
import main.java.physics.AABB;

import java.util.ArrayList;

/**
 * A one unit wide pillar of Tile's. The height of these are calculated in World#generateWorld. World also holds a list of these.
 */
public class WorldTile extends Entity {

    private ArrayList<Tile> tiles;
    private AABB aabb;

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
        this.tiles.add(new Tile(position, Models.GRASS_TILE));
        for (float i = position.subtractY(1.0f).y; i >= 0.0f; --i){
            this.tiles.add(new Tile(new Vector3f(position.x, i), Models.DIRT_TILE));
        }
        this.aabb = new AABB(1.0f, position.y);
    }

    /**
     * Gets the list of Tile's.
     * @return the list of Tile's.
     */
    ArrayList<Tile> getTiles(){
        return this.tiles;
    }

    @Override
    void updateMatrix() {
        super.updateMatrix();
        this.tiles.forEach(Tile::updateMatrix);
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
        this.tiles.forEach(Tile::cleanUp);
    }
}