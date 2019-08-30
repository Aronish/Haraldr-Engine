package com.game.gameobject.tile;

import com.game.gameobject.GameObject;
import com.game.math.Vector3f;

class TileGrass extends Tile {

    TileGrass(Vector3f position){
        super(position, GameObject.GRASS);
    }
}