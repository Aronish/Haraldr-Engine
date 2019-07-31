package com.game.level.gameobject.tile;

import com.game.level.gameobject.EnumGameObjects;
import com.game.math.Vector3f;

class TileStone extends Tile {

    TileStone(Vector3f position){
        super(position, EnumGameObjects.STONE);
    }
}
