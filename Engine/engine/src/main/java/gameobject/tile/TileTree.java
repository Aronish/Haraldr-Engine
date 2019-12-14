package gameobject.tile;

import gameobject.GameObject;
import gameobject.IBackground;
import math.Vector3f;

public class TileTree extends Tile implements IBackground {

    public TileTree(Vector3f position){
        super(position, GameObject.TREE);
    }
}
