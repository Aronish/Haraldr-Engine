package oldgame.gameobject.tile;

import oldgame.gameobject.GameObject;
import haraldr.math.Vector3f;

class TileGrassSnow extends Tile {

    TileGrassSnow(Vector3f position){
        super(position, GameObject.GRASS_SNOW);
    }
}
