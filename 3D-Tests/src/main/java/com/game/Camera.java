package com.game;

import com.game.gameobject.tile.Tile;
import com.game.math.Matrix4f;
import com.game.math.Vector2f;
import com.game.math.Vector3f;

import java.util.List;

import static com.game.Main.fastFloor;

/**
 * A virtual camera for the game. Is essentially a normal object except that the transformation matrix is inverted.
 * That matrix is then applied to all objects in the scene to make it appear as if the camera was moving.
 */
public class Camera {

    private Matrix4f viewMatrix;
    private Vector3f position;
    private float rotation;
    private float scale;
    private Vector2f scaleVector;
    private int chunkXRange, chunkYRange;

    private static final float MIN_SCALE = 0.125f;
    private static final float MAX_SCALE = 2.0f;
    private static final float SCALE_SPEED = 1.0f;

    public Camera(){
        this(new Vector3f(), 0.0f, 0.5f);
    }

    public Camera(float scale){
        this(new Vector3f(), 0.0f, scale);
    }

    private Camera(Vector3f position, float rotation, float scale){
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        scaleVector = new Vector2f(scale);
        calculateChunkRanges();
        calculateViewMatrix();
    }

    private void calculateViewMatrix(){
        viewMatrix = Matrix4f.transform(position, rotation, scaleVector, true);
    }

    /**
     * Calculates the scale/zoom based on the delta time from Application#update. Also takes scale into account to make the speed constant.
     * Constants are defined in the top of this class.
     * @param shouldIncrease whether the scale should increase or not. If true, it increases, meaning zooming in.
     * @param deltaTime the delta time used to make the scaling uniform.
     */
    public void calculateScale(boolean shouldIncrease, float deltaTime){
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
    private void calculateChunkRanges(){
        chunkXRange = fastFloor(1.0f / scale);
        chunkYRange = fastFloor(1.0f / scale);
    }

    public void setPosition(Vector3f pos){
        position = pos.multiply(scale);
        calculateViewMatrix();
    }

    public void addPosition(Vector3f pos){
        position.add(pos);
        calculateViewMatrix();
    }

    public void setScale(float scal){
        scale = scal;
        scaleVector.setBoth(scale);
        calculateViewMatrix();
    }

    /**
     * Checks if the provided entities are in the view of the Camera. If they are, they are added to the provided list.
     * @param visibleObjects a list, which keeps track of all the visible entities.
     * @param tile the tile to check visibility against.
     */
    public void isInView(List<Tile> visibleObjects, Tile tile){
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

    public Matrix4f getViewMatrix(){
        return viewMatrix;
    }

    public float getScale(){
        return scale;
    }

    public int getChunkXRange(){
        return chunkXRange;
    }

    public int getChunkYRange(){
        return chunkYRange;
    }
}
