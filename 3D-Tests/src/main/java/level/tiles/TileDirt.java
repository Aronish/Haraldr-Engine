package main.java.level.tiles;

import main.java.graphics.Models;
import main.java.math.Vector3f;

class TileDirt extends Tile {

    TileDirt(Vector3f position){
        super(position, EnumTiles.DIRT, Models.DIRT_TILE);
    }
}
