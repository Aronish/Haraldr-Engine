package com.game.level.gameobject.tile;

import com.game.level.gameobject.EnumGameObjects;
import com.game.math.Vector3f;

class TileGrass extends Tile {

    TileGrass(Vector3f position){
        super(position, EnumGameObjects.GRASS);
    }
}
