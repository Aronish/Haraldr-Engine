package com.game.gameobject;

import com.game.graphics.Model;
import com.game.graphics.Models;

/**
 * All possible types of game objects. Holds a reference to the Model to be associated with the object.
 */
public enum GameObject
{
    ///// WORLD TILES /////////////////////////////////
    GRASS(Models.GRASS_TILE, true),
    DIRT(Models.DIRT_TILE, true),
    STONE(Models.STONE_TILE, true),
    GRASS_SNOW(Models.GRASS_SNOW_TILE, true),
    TREE(Models.TREE, true),
    GRASS_TUFT(Models.GRASS_TUFT, true),

    ///// MOVABLES ////////////////////////////////////
    PLAYER(Models.PLAYER, false);

    private final Model model;
    public final boolean instanced;

    GameObject(Model model, boolean instanced)
    {
        this.model = model;
        this.instanced = instanced;
    }

    public Model getModel()
    {
        return model;
    }
}