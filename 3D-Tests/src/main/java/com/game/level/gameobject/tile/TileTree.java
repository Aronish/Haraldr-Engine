package com.game.level.gameobject.tile;

import com.game.level.IBackground;
import com.game.level.gameobject.GameObject;
import com.game.math.Vector3f;

public class TileTree extends Tile implements IBackground {

    public TileTree(Vector3f position){
        super(position, GameObject.TREE);
    }
}
