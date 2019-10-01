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
public class Camera
{
    private final float[] zooms = { 0.125f, 0.25f, 0.5f, 1.0f, 2.0f, 4.0f };

    private Matrix4f viewMatrix;
    private Vector3f position;
    private int currentZoom = 2;
    private float scale = zooms[currentZoom];
    private Vector2f scaleVector = new Vector2f();
    private int chunkXRange, chunkYRange;

    public Camera()
    {
        this(new Vector3f());
    }

    private Camera(Vector3f position)
    {
        this.position = position;
        calculateChunkRanges();
        calculateViewMatrix();
    }

    private void calculateViewMatrix()
    {
        viewMatrix = Matrix4f.transform(position, 0.0f, scaleVector.setBoth(scale), true);
    }

    public void zoomIn()
    {
        ++currentZoom;
        if (currentZoom >= zooms.length) currentZoom = zooms.length - 1;
        scale = zooms[currentZoom];
        calculateChunkRanges();
        calculateViewMatrix();
    }

    public void zoomOut()
    {
        --currentZoom;
        if (currentZoom < 0) currentZoom = 0;
        scale = zooms[currentZoom];
        calculateChunkRanges();
        calculateViewMatrix();
    }

    /**
     * Calculates how many GridCells/chunks should be visible.
     */
    private void calculateChunkRanges()
    {
        chunkXRange = fastFloor(1.0f / scale);
        chunkYRange = fastFloor(1.0f / scale);
        if (chunkXRange < 1) chunkXRange = 1;
        if (chunkYRange < 1) chunkYRange = 1;
    }

    public void setPosition(Vector3f position)
    {
        this.position = position.multiply(scale);
        calculateViewMatrix();
    }

    public void addPosition(Vector3f pos)
    {
        position.add(pos);
        calculateViewMatrix();
    }

    public void setScale(float scale)
    {
        this.scale = scale;
        scaleVector.setBoth(scale);
        calculateViewMatrix();
    }

    /**
     * Checks if the provided entities are in the view of the Camera. If they are, they are added to the provided list.
     * @param visibleObjects a list, which keeps track of all the visible entities.
     * @param tile the tile to check visibility against.
     */
    public void isInView(List<Tile> visibleObjects, Tile tile)
    {
        float scaleAdjustedX = position.getX() / scale;
        float scaleAdjustedY = position.getY() / scale;
        float xBoundary = 16.0f / scale;
        float yBoundary = 9.0f / scale;
        boolean collisionX = tile.getPosition().getX() + tile.getGameObjectType().getModel().getAABB().getWidth() > scaleAdjustedX - xBoundary && tile.getPosition().getX() < scaleAdjustedX + xBoundary;
        boolean collisionY = tile.getPosition().getY() - tile.getGameObjectType().getModel().getAABB().getHeight() < scaleAdjustedY + yBoundary && tile.getPosition().getY() > scaleAdjustedY - yBoundary;
        if (collisionX && collisionY)
        {
            visibleObjects.add(tile);
        }
    }

    public Matrix4f getViewMatrix()
    {
        return viewMatrix;
    }

    public float getScale()
    {
        return scale;
    }

    public int getChunkXRange()
    {
        return chunkXRange;
    }

    public int getChunkYRange()
    {
        return chunkYRange;
    }
}
