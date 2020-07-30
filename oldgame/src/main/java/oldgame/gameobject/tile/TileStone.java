package oldgame.gameobject.tile;

import oldgame.gameobject.GameObject;
import haraldr.math.Vector3f;

class TileStone extends Tile {

    TileStone(Vector3f position){
        super(position, GameObject.STONE);
    }
}
