package main.java.level.tiles;

import main.java.graphics.Models;
import main.java.math.Vector3f;

class TileGrass extends Tile {

    TileGrass(Vector3f position){
        super(position, EnumTiles.GRASS, Models.GRASS_TILE);
    }
}
