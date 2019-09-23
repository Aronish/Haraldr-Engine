package com.game.gameobject;

import com.game.graphics.IModel;
import com.game.graphics.ModelImpr;
import com.game.graphics.Models;
import com.game.graphics.Model;

import static com.game.Application.MAIN_LOGGER;

/**
 * All possible types of game objects. Holds a reference to the Model to be associated with the object.
 */
public enum GameObject
{
    ///// WORLD TILES /////////////////////////////////
    GRASS(Models.GRASS_TILE),
    DIRT(Models.DIRT_TILE),
    STONE(Models.STONE_TILE),
    GRASS_SNOW(Models.GRASS_SNOW_TILE),
    TREE(Models.TREE),
    GRASS_TUFT(Models.GRASS_TUFT),

    ///// MOVABLES ////////////////////////////////////
    PLAYER(Models.PLAYER_IMPR);

    public final Model model;
    public final ModelImpr modelImpr;

    GameObject(Model model)
    {
        this.model = model;
        this.modelImpr = null;
    }

    GameObject(ModelImpr modelImpr)
    {
        this.modelImpr = modelImpr;
        this.model = null;
    }

    public IModel getModel()
    {
        if (model != null)
        {
            return model;
        }
        else return modelImpr;
    }
}