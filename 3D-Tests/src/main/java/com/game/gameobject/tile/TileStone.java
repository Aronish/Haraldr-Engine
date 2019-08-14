package com.game.gameobject.tile;

import com.game.gameobject.GameObject;
import com.game.math.Vector3f;

class TileStone extends Tile {

    TileStone(Vector3f position){
        super(position, GameObject.STONE);
    }
}
