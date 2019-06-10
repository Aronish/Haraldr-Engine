package main.java.level;

import main.java.graphics.Models;
import main.java.graphics.TexturedModel;
import main.java.math.Vector3f;

/**
 * Simple 1x1 square Entity.
 */
class Tile extends Entity {

    /**
     * Constructor with just the position.
     * @param position the initial position of this Tile.
     */
    Tile(Vector3f position){
        this(position, 0.0f, 1.0f, Models.GRASS_TILE);
    }

    /**
     * Constructor with the position and eventual TexturedModel's.
     * @param position the initial position of this Tile.
     * @param texturedModels eventual TexturedModel's.
     */
    Tile(Vector3f position, TexturedModel... texturedModels){
        this(position, 0.0f, 1.0f, texturedModels);
    }

    /**
     * Constructor with position, rotation and scale parameters.
     * @param position the initial position of this Tile.
     * @param rotation the initial rotation of this Tile.
     * @param scale the initial scale of this Tile.
     * @param texturedModels eventual TexturedModel's.
     */
    private Tile(Vector3f position, float rotation, float scale, TexturedModel... texturedModels){
        super(position, rotation, scale, texturedModels);
    }
}
