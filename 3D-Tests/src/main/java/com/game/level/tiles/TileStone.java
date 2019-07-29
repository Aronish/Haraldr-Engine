package com.game.level.tiles;

import com.game.graphics.Models;
import com.game.math.Vector3f;

class TileStone extends Tile {

    TileStone(Vector3f position){
        super(position, EnumTiles.STONE, Models.STONE_TILE);
    }
}
