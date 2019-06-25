package main.java.level.tiles;

import main.java.graphics.Models;
import main.java.math.Vector3f;

class TileStone extends Tile {

    TileStone(Vector3f position){
        super(position, 1.0f, Models.STONE_TILE);
    }

    TileStone(Vector3f position, float scale) {
        super(position, scale, Models.STONE_TILE);
    }
}
