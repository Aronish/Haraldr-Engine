package gameobject.tile;

import gameobject.GameObject;
import math.Vector3f;

class TileGrassSnow extends Tile {

    TileGrassSnow(Vector3f position){
        super(position, GameObject.GRASS_SNOW);
    }
}
