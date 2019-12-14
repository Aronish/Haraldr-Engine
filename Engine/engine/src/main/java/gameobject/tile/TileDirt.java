package gameobject.tile;

import gameobject.GameObject;
import math.Vector3f;

class TileDirt extends Tile {

    TileDirt(Vector3f position){
        super(position, GameObject.DIRT);
    }
}
