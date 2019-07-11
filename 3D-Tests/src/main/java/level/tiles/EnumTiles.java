package main.java.level.tiles;

import main.java.graphics.Models;
import main.java.graphics.TexturedModel;

/**
 * All possible types of Tiles. Holds a reference to the TexturedModel to be associated with the Tile.
 */
public enum EnumTiles {

    GRASS(Models.GRASS_TILE),
    DIRT(Models.DIRT_TILE),
    STONE(Models.STONE_TILE),
    GRASS_SNOW(Models.GRASS_SNOW_TILE),
    TREE(Models.getTREE());

    public final TexturedModel texturedModel;

    EnumTiles(TexturedModel texturedModel){
        this.texturedModel = texturedModel;
    }
}