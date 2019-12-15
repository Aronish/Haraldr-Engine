package oldgame.gameobject.tile;

import oldgame.gameobject.GameObject;
import engine.math.Vector3f;

class TileGrassSnow extends Tile {

    TileGrassSnow(Vector3f position){
        super(position, GameObject.GRASS_SNOW);
    }
}
