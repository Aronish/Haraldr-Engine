package com.game;

import com.game.level.gameobject.tile.Tile;
import com.game.math.Matrix4f;
import com.game.math.Vector2f;
import com.game.math.Vector3f;

import java.util.ArrayList;

import static com.game.Main.fastFloor;

/**
 * A virtual camera for the game. Is essentially a normal object except that the transformation matrix is inverted.
 * That matrix is then applied to all objects in the scene to make it appear as if the camera was moving.
 */
public class Camera{

    public static Matrix4f viewMatrix;
    private static Vector3f position;
    private static float rotation;
    public static float scale;
    private static Vector2f scaleVector;
    public static int chunkXRange, chunkYRange;

    private static final float MIN_SCALE = 0.125f;
    private static final float MAX_SCALE = 2.0f;
    private static final float SCALE_SPEED = 1.0f;

    /**
     * Default constructor if no arguments are provided.
     */
    Camera(){
        this(new Vector3f(), 0.0f, 1.0f);
    }

    /**
     * Constructor with just the scale.
     * @param scale the scale to scale the world by.
     */
    Camera(float scale){
        this(new Vector3f(), 0.0f, scale);
    }

    /**
     * Constructor with parameters for position and rotation.
     * @param pos the initial position of the camera, an origin vector.
     * @param rot the initial rotation of the camera around the z-axis, in degrees.
     */
    private Camera(Vector3f pos, float rot, float scal){
        position = pos;
        rotation = rot;
        scale = scal;
        scaleVector = new Vector2f(scale);
        calculateChunkRanges();
        calculateViewMatrix();
    }

    /**
     * Calculates a new view matrix (an inverted transformation matrix) from the current attribute values.
     */
    private static void calculateViewMatrix(){
        viewMatrix = Matrix4f.transform(position, rotation, scaleVector, true);
    }

    /**
     * Calculates the scale/zoom based on the delta time from Application#update. Also takes scale into account to make the speed constant.
     * Constants are defined in the top of this class.
     * @param shouldIncrease whether the scale should increase or not. If true, it increases, meaning zooming in.
     * @param deltaTime the delta time used to make the scaling uniform.
     */
    static void calculateScale(boolean shouldIncrease, float deltaTime){
        if (shouldIncrease){
            scale += SCALE_SPEED * scale * deltaTime;
            if (scale > MAX_SCALE){
                scale = MAX_SCALE;
            }
        }else {
            scale -= SCALE_SPEED * scale * deltaTime;
            if (scale < MIN_SCALE){
                scale = MIN_SCALE;
            }
        }
        scaleVector.setBoth(scale);
        calculateChunkRanges();
        calculateViewMatrix();
    }

    /**
     * Calculates how many GridCells/chunks should be visible.
     */
    private static void calculateChunkRanges(){
        chunkXRange = fastFloor(1.0f / Camera.scale);
        chunkYRange = fastFloor(1.0f / Camera.scale);
    }

    /**
     * Sets the position of this Camera.
     * @param pos the new position vector.
     */
    public static void setPosition(Vector3f pos){
        position = pos.multiply(scale);
        calculateViewMatrix();
    }

    /**
     * Adds the specified vector to the current position.
     * @param pos the position to add.
     */
    public static void addPosition(Vector3f pos){
        position.addThis(pos);
        calculateViewMatrix();
    }

    /**
     * Sets the scale of this Camera.
     * @param scal the new scale.
     */
    public static void setScale(float scal){
        scale = scal;
        scaleVector.setBoth(scale);
        calculateViewMatrix();
    }

    /**
     * Checks if the provided entities are in the view of the Camera. If they are, they are added to the provided list.
     * @param visibleObjects a list, which keeps track of all the visible entities.
     * @param tile the tile to check visibility against.
     */
    public static void isInView(ArrayList<Tile> visibleObjects, Tile tile){
        float scaleAdjustedX = position.getX() / scale;
        float scaleAdjustedY = position.getY() / scale;
        float xBoundary = 16.0f / scale;
        float yBoundary = 9.0f / scale;
        boolean collisionX = tile.getPosition().getX() + tile.getGameObjectType().model.getAABB().getWidth() > scaleAdjustedX - xBoundary && tile.getPosition().getX() < scaleAdjustedX + xBoundary;
        boolean collisionY = tile.getPosition().getY() - tile.getGameObjectType().model.getAABB().getHeight() < scaleAdjustedY + yBoundary && tile.getPosition().getY() > scaleAdjustedY - yBoundary;
        if (collisionX && collisionY){
            visibleObjects.add(tile);
        }
    }
}