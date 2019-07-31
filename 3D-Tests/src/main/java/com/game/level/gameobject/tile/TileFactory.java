package com.game.level.gameobject.tile;

import com.game.debug.Logger;
import com.game.level.gameobject.EnumGameObjects;
import com.game.math.Vector3f;

public class TileFactory {

    /**
     * Constructs a new Tile with the specified type and position.
     * Avoids huge if statements if different tiles need to be generated based on certain conditions.
     * @param position the initial position of the Tile.
     * @param tileType the type of the Tile.
     * @return the new Tile.
     */
    public Tile createTile(Vector3f position, EnumGameObjects tileType){
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
                Logger.error("Invalid TileType");
                return null;
        }
    }
}
