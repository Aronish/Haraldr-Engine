package com.game.gameobject.tile;

import com.game.gameobject.GameObject;
import com.game.math.Vector3f;

class TileDirt extends Tile {

    TileDirt(Vector3f position){
        super(position, GameObject.DIRT);
    }
}
