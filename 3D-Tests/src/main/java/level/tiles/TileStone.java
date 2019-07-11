package main.java.level.tiles;

import main.java.graphics.Models;
import main.java.math.Vector3f;

class TileStone extends Tile {

    TileStone(Vector3f position){
        super(position, EnumTiles.STONE, Models.STONE_TILE);
    }
}
