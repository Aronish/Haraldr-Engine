package com.game.level.tiles;

import com.game.graphics.Models;
import com.game.level.IBackground;
import com.game.math.Vector3f;

public class TileTree extends Tile implements IBackground {

    public TileTree(Vector3f position){
        super(position, EnumTiles.TREE, Models.TREE);
    }
}
