package main.java.level;

import main.java.graphics.Models;
import main.java.math.Vector3f;
import main.java.physics.AABB;

import java.util.ArrayList;

public class WorldTile extends Entity {

    private ArrayList<Tile> tiles;
    private AABB aabb;

    WorldTile(Vector3f position){
        this(position, 0.0f, 1.0f);
    }

    private WorldTile(Vector3f position, float rotation, float scale) {
        super(position, rotation, scale);
        this.tiles = new ArrayList<>();
        this.tiles.add(new Tile(position, Models.GRASS_TILE));
        for (float i = position.subtractY(1.0f).y; i >= 0.0f; --i){
            this.tiles.add(new Tile(new Vector3f(position.x, i), Models.DIRT_TILE));
        }
        this.aabb = new AABB(1.0f, position.y);
    }

    public ArrayList<Tile> getTiles(){
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
}