package com.game.level.gameobject.tile;

import com.game.level.gameobject.EnumGameObjects;
import com.game.math.Vector3f;

class TileDirt extends Tile {

    TileDirt(Vector3f position){
        super(position, EnumGameObjects.DIRT);
    }
}
