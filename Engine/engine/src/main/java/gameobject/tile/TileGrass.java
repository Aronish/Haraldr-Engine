package gameobject.tile;

import gameobject.GameObject;
import math.Vector3f;

class TileGrass extends Tile {

    TileGrass(Vector3f position){
        super(position, GameObject.GRASS);
    }
}
