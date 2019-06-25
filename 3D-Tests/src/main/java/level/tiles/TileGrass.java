package main.java.level.tiles;

import main.java.graphics.Models;
import main.java.math.Vector3f;

class TileGrass extends Tile {

    TileGrass(Vector3f position){
        super(position, 1.0f, Models.GRASS_TILE);
    }

    TileGrass(Vector3f position, float scale) {
        super(position, scale, Models.GRASS_TILE);
    }
}
