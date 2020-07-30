package oldgame.gameobject.tile;

import oldgame.gameobject.GameObject;
import haraldr.math.Vector3f;

class TileDirt extends Tile {

    TileDirt(Vector3f position){
        super(position, GameObject.DIRT);
    }
}
