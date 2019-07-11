package main.java.level.tiles;

import main.java.graphics.Models;
import main.java.math.Vector3f;

class TileGrassSnow extends Tile {

    TileGrassSnow(Vector3f position){
        super(position, EnumTiles.GRASS_SNOW, Models.GRASS_SNOW_TILE);
    }
}
