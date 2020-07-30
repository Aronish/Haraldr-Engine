package oldgame.gameobject.tile;

import oldgame.gameobject.Entity;
import oldgame.gameobject.GameObject;
import haraldr.math.Vector3f;

/**
 * Simple static entity.
 */
public abstract class Tile extends Entity {

    /**
     * Constructor without rotation and scale parameters.
     * @param position the initial position of this Tile.
     * @param tileType the type of this Tile.
     */
    Tile(Vector3f position, GameObject tileType){
        this(position, 0.0f, 1.0f, tileType);
    }

    /**
     * Constructor with position, rotation and scale parameters.
     * @param position the initial position of this Tile.
     * @param rotation the initial rotation of this Tile.
     * @param scale the initial scale of this Tile.
     * @param gameObjectType the type of this Tile.
     */
    private Tile(Vector3f position, float rotation, float scale, GameObject gameObjectType) {
        super(position, rotation, scale, gameObjectType);
    }
}
