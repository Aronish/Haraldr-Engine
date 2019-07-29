package com.game.level.tiles;

import com.game.graphics.Models;
import com.game.math.Vector3f;

class TileGrassSnow extends Tile {

    TileGrassSnow(Vector3f position){
        super(position, EnumTiles.GRASS_SNOW, Models.GRASS_SNOW_TILE);
    }
}
