package main.java.level.tiles;

import main.java.graphics.Models;
import main.java.math.Vector3f;

class TileDirt extends Tile {

    TileDirt(Vector3f position){
        super(position, 1.0f, Models.DIRT_TILE);
    }

    TileDirt(Vector3f position, float scale) {
        super(position, scale, Models.DIRT_TILE);
    }
}
