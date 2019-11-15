package com.game.world;

import com.game.gameobject.GameObject;
import com.game.gameobject.tile.Tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.game.Main.fastFloor;

/**
 * Contains all objects int the World in a grid system.
 */
public class Grid
{
    public static final int GRID_SIZE = 8; // 8 seems like the sweetspot.

    private int width, height;

    private List<List<GridCell>> grid = new ArrayList<>();

    /**
     * Populates the grid with the supplied tiles. Caches the matrices in the grid cells.
     * @param tiles the tiles to be put in the grid.
     */
    void populateGrid(List<Tile> tiles)
    {
        tiles.forEach(entity -> addEntity(fastFloor(entity.getPosition().getX() / GRID_SIZE), fastFloor(entity.getPosition().getY() / GRID_SIZE), entity));
        cacheMatrices();
    }

    /**
     * Creates a store of matrices in the grid cells to avoid dynamic queries based on game object type.
     */
    private void cacheMatrices()
    {
        for (List<GridCell> yAxis : grid)
        {
            for (GridCell gridCell : yAxis)
            {
                gridCell.cacheMatrices();
            }
        }
    }

    /**
     * Adds a tile at the grid coordinate (x, y). Has bounds checking.
     * If x is larger than the current width, another x list is added with the current height.
     * If y is larger than the current height, heights of all x lists are increased by one.
     */
    private void addEntity(int x, int y, Tile tile)
    {
        boolean succeeded = false;
        while (!succeeded)
        {
            if (width <= x)
            {
                grid.add(new ArrayList<>());
                List<GridCell> last = grid.get(grid.size() - 1);
                for (int i = 0; i <= height; ++i)
                {
                    last.add(new GridCell());
                }
                ++width;
                continue;
            }
            if (height <= y)
            {
                grid.forEach(xAxis -> xAxis.add(new GridCell()));
                ++height;
                continue;
            }
            grid.get(x).get(y).addEntity(tile);
            succeeded = true;
        }
    }

    public void clear()
    {
        width = 0;
        height = 0;
        grid = null;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidthI()
    {
        return width - 1;
    }

    public int getHeightI()
    {
        return height - 1;
    }

    public GridCell getGridCell(int x, int y)
    {
        return grid.get(x).get(y);
    }

    /**
     * A cell in the Grid that contains Tiles. Necessary for instanced rendering as matrices need to be collected.
     * At the moment it can only contain Tiles.
     */
    public static class GridCell
    {
        private List<Tile> tiles = new ArrayList<>();

        private Map<GameObject, List<Float>> matrices = new HashMap<>();
        private List<Float> intermeditateArray = new ArrayList<>();

        private Map<GameObject, Integer> tileCounts = new HashMap<>();

        private GridCell() {}

        private void addEntity(Tile tile)
        {
            tiles.add(tile);
        }

        /**
         * Creates a HashMap that, for every type of game object, stores all matrices of that type with the type as the key.
         */
        private void cacheMatrices()
        {
            for (GameObject tileType : GameObject.instancedObjects)
            {
                int tileCount = 0;
                intermeditateArray.clear();
                for (Tile tile : tiles)
                {
                    if (tile.getGameObjectType() == tileType)
                    {
                        ++tileCount;
                        for (float element : tile.getMatrixArray())
                        {
                            intermeditateArray.add(element);
                        }
                    }
                }
                matrices.put(tileType, new ArrayList<>(intermeditateArray));
                tileCounts.put(tileType, tileCount);
            }
        }

        public List<Tile> getTiles()
        {
            return tiles;
        }

        public List<Float> getMatrices(GameObject tileType)
        {
            return matrices.get(tileType);
        }

        public Integer getTileCount(GameObject gameObject)
        {
            return tileCounts.get(gameObject);
        }
    }
}
