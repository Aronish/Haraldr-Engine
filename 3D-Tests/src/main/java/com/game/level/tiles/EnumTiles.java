package com.game.level.tiles;

import com.game.graphics.Models;
import com.game.graphics.TexturedModel;

/**
 * All possible types of Tiles. Holds a reference to the TexturedModel to be associated with the Tile.
 */
public enum EnumTiles {

    GRASS(Models.GRASS_TILE),
    DIRT(Models.DIRT_TILE),
    STONE(Models.STONE_TILE),
    GRASS_SNOW(Models.GRASS_SNOW_TILE),
    TREE(Models.TREE);

    public final TexturedModel texturedModel;

    EnumTiles(TexturedModel texturedModel){
        this.texturedModel = texturedModel;
    }

}