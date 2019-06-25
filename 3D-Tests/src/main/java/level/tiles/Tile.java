package main.java.level.tiles;

import main.java.graphics.TexturedModel;
import main.java.level.Entity;
import main.java.math.Vector3f;

/**
 * Simple 1x1 square Entity.
 */
public abstract class Tile extends Entity {

    Tile(Vector3f position, float scale, TexturedModel... texturedModels){
        this(position, 0.0f, scale, texturedModels);
    }

    /**
     * Constructor with position, rotation and scale parameters.
     * @param position the initial position of this Tile.
     * @param rotation the initial rotation of this Tile.
     * @param scale the initial scale of this Tile.
     * @param texturedModels eventual TexturedModel's.
     */
    private Tile(Vector3f position, float rotation, float scale, TexturedModel... texturedModels) {
        super(position, rotation, scale, texturedModels);
    }
}
