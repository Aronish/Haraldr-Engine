package main.java.level.tiles;

import main.java.debug.Logger;
import main.java.math.Vector3f;

public class TileFactory {

    public Tile createTile(Vector3f position, EnumTiles tileType){
        switch (tileType){
            case DIRT:
                return new TileDirt(position);
            case GRASS:
                return new TileGrass(position);
            case STONE:
                return new TileStone(position);
            case GRASS_SNOW:
                return new TileGrassSnow(position);
            default:
                Logger.setErrorLevel();
                Logger.log("Invalid TileType");
                return null;
        }
    }
}
