package com.game.gameobject.tile;

import com.game.gameobject.GameObject;
import com.game.math.Vector3f;

class TileGrassSnow extends Tile {

    TileGrassSnow(Vector3f position){
        super(position, GameObject.GRASS_SNOW);
    }
}
