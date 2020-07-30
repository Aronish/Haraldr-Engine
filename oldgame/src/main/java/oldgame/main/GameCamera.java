package oldgame.main;

import haraldr.main.OrthographicCamera;
import haraldr.main.EntryPoint;
import haraldr.math.Matrix4f;
import haraldr.math.Vector3f;
import oldgame.gameobject.tile.Tile;
import oldgame.world.Grid;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GameCamera extends OrthographicCamera
{
    private static final float[] zooms = { 0.125f, 0.25f, 0.5f, 1.0f, 2.0f, 4.0f };

    private int currentZoom = 2;
    private int chunkXRange, chunkYRange;

    public GameCamera()
    {
        this(new Vector3f());
    }

    public GameCamera(Vector3f position)
    {
        super(position);
        scale = zooms[currentZoom];
        calculateChunkRanges();
    }

    /**
     * Calculates how many GridCells/chunks should be rendered.
     */
    private void calculateChunkRanges()
    {
        chunkXRange = engine.main.EntryPoint.fastCeil((Matrix4f.FIXED_ORTHOGRAPHIC_AXIS / scale) / Grid.GRID_SIZE);
        chunkYRange = EntryPoint.fastCeil((Matrix4f.dynamicOrthographicAxis / scale) / Grid.GRID_SIZE);
    }

    /**
     * Checks if the provided entities are in the view of the Camera. If they are, they are added to the provided list.
     * @param visibleObjects a list, which keeps track of all the visible entities.
     * @param tile the tile to check visibility against.
     */
    public void isInView(List<Tile> visibleObjects, @NotNull Tile tile)
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

    public void zoomIn()
    {
        ++currentZoom;
        if (currentZoom >= zooms.length) currentZoom = zooms.length - 1;
        scale = zooms[currentZoom];
        calculateViewMatrix();
    }

    public void zoomOut()
    {
        --currentZoom;
        if (currentZoom < 0) currentZoom = 0;
        scale = zooms[currentZoom];
        calculateViewMatrix();
        calculateChunkRanges();
    }

    public void setScale(float scale)
    {
        this.scale = scale;
        scaleVector.set(scale);
        calculateViewMatrix();
        calculateChunkRanges();
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
