package gameobject.tile;

import gameobject.GameObject;
import math.Vector3f;

class TileStone extends Tile {

    TileStone(Vector3f position){
        super(position, GameObject.STONE);
    }
}
