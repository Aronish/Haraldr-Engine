package main.java.level.tiles;

import main.java.graphics.TexturedModel;
import main.java.level.Entity;
import main.java.math.Vector3f;

/**
 * Simple static entity.
 */
public abstract class Tile extends Entity {

    private EnumTiles tileType;

    /**
     * Constructor without rotation and scale parameters.
     * @param position the initial position of this Tile.
     * @param tileType the type of this Tile.
     * @param texturedModels eventual TexturedModel's.
     */
    Tile(Vector3f position, EnumTiles tileType, TexturedModel... texturedModels){
        this(position, 0.0f, 1.0f, tileType, texturedModels);
    }

    /**
     * Constructor without rotation parameter.
     * @param position the initial position of this Tile.
     * @param scale the initial scale of this Tile.
     * @param tileType the type of this Tile.
     * @param texturedModels eventual TexturedModel's.
     */
    Tile(Vector3f position, float scale, EnumTiles tileType, TexturedModel... texturedModels){
        this(position, 0.0f, scale, tileType, texturedModels);
    }

    /**
     * Constructor with position, rotation and scale parameters.
     * @param position the initial position of this Tile.
     * @param rotation the initial rotation of this Tile.
     * @param scale the initial scale of this Tile.
     * @param tileType the type of this Tile.
     * @param texturedModels eventual TexturedModel's.
     */
    private Tile(Vector3f position, float rotation, float scale, EnumTiles tileType, TexturedModel... texturedModels) {
        super(position, rotation, scale, texturedModels);
        this.tileType = tileType;
    }

    /**
     * @return the type of this Tile.
     */
    public EnumTiles getTileType(){
        return this.tileType;
    }
}
