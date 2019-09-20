package com.game.gameobject.tile;

import com.game.gameobject.IBackground;
import com.game.gameobject.GameObject;
import com.game.math.Vector3f;

public class TileTree extends Tile implements IBackground {

    public TileTree(Vector3f position){
        super(position, GameObject.TREE);
    }
}
