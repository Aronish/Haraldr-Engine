package main.java.level.tiles;

import main.java.graphics.Models;
import main.java.math.Vector3f;

public class TileGrassSnow extends Tile {

    TileGrassSnow(Vector3f position){
        super(position, 1.0f, Models.SNOW_TILE);
    }

    TileGrassSnow(Vector3f position, float scale) {
        super(position, scale, Models.SNOW_TILE);
    }
}
