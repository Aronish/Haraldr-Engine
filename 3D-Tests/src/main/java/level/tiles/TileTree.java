package main.java.level.tiles;

import main.java.graphics.Models;
import main.java.level.IBackground;
import main.java.math.Vector3f;

public class TileTree extends Tile implements IBackground {

    public TileTree(Vector3f position){
        super(position, EnumTiles.TREE, Models.getTREE());
    }
}
