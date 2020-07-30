package oldgame.gameobject.tile;

import oldgame.gameobject.GameObject;
import engine.math.Vector3f;

class TileGrass extends Tile {

    TileGrass(Vector3f position){
        super(position, GameObject.GRASS);
    }
}
