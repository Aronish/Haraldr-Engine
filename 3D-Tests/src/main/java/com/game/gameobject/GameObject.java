package com.game.gameobject;

import com.game.graphics.Models;
import com.game.graphics.Model;

/**
 * All possible types of game objects. Holds a reference to the Model to be associated with the object.
 */
public enum GameObject {

    ///// WORLD TILES /////////////////////////////////
    GRASS(Models.GRASS_TILE),
    DIRT(Models.DIRT_TILE),
    STONE(Models.STONE_TILE),
    GRASS_SNOW(Models.GRASS_SNOW_TILE),
    TREE(Models.TREE),
    GRASS_TUFT(Models.GRASS_TUFT),

    ///// MOVABLES ////////////////////////////////////
    PLAYER(Models.PLAYER);

    public final Model model;

    GameObject(Model model){
        this.model = model;
    }
}