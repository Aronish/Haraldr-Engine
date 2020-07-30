package oldgame.gameobject.tile;

import oldgame.gameobject.GameObject;
import oldgame.gameobject.IBackground;
import engine.math.Vector3f;

public class TileTree extends Tile implements IBackground {

    public TileTree(Vector3f position){
        super(position, GameObject.TREE);
    }
}
