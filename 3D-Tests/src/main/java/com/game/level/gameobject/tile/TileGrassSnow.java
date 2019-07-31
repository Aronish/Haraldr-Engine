package com.game.level.gameobject.tile;

import com.game.level.gameobject.EnumGameObjects;
import com.game.math.Vector3f;

class TileGrassSnow extends Tile {

    TileGrassSnow(Vector3f position){
        super(position, EnumGameObjects.GRASS_SNOW);
    }
}
