package com.game.level.tiles;

import com.game.graphics.Models;
import com.game.math.Vector3f;

class TileGrass extends Tile {

    TileGrass(Vector3f position){
        super(position, EnumTiles.GRASS, Models.GRASS_TILE);
    }
}
