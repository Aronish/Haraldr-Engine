package com.game.level.tiles;

import com.game.graphics.Models;
import com.game.math.Vector3f;

class TileDirt extends Tile {

    TileDirt(Vector3f position){
        super(position, EnumTiles.DIRT, Models.DIRT_TILE);
    }
}
