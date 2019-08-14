package com.game.world;

import com.game.gameobject.GameObject;
import com.game.gameobject.tile.Tile;

import java.util.ArrayList;
import java.util.HashMap;

import static com.game.Main.fastFloor;

/**
 * Contains all objects int the World in a grid system.
 */
public class Grid {

    public static final int GRID_SIZE = 20;

    private int width, height;

    private ArrayList<ArrayList<GridCell>> grid;

    Grid(){
        width = 0;
        height = 0;
        grid = new ArrayList<>();
    }

    /**
     * Populates the grid with the supplied tiles. Caches the matrices in the grid cells.
     * @param tiles the tiles to be put in the grid.
     */
    void populateGrid(ArrayList<Tile> tiles){
        tiles.forEach(entity -> addEntity(fastFloor(entity.getPosition().getX() / GRID_SIZE), fastFloor(entity.getPosition().getY() / GRID_SIZE), entity));
        cacheMatrices();
    }

    /**
     * Creates a store of matrices in the grid cells to avoid dynamic queries based on game object type.
     */
    private void cacheMatrices(){
        for (ArrayList<GridCell> yAxis : grid){
            for (GridCell gridCell : yAxis){
                gridCell.cacheMatrices();
            }
        }
    }

    /**
     * Adds a tile at the grid coordinate (x, y). Has bounds checking.
     * If x is larger than the current width, another x list is added with the current height.
     * If y is larger than the current height, heights of all x lists are increased by one.
     */
    private void addEntity(int x, int y, Tile tile){
        boolean succeeded = false;
        while (!succeeded){
            if (width <= x){
                grid.add(new ArrayList<>());
                ArrayList<GridCell> last = grid.get(grid.size() - 1);
                for (int i = 0; i <= height; ++i){
                    last.add(new GridCell());
                }
                ++width;
                continue;
            }
            if (height <= y){
                grid.forEach(xAxis -> xAxis.add(new GridCell()));
                ++height;
                continue;
            }
            grid.get(x).get(y).addEntity(tile);
            succeeded = true;
        }
    }

    void clear(){
        width = 0;
        height = 0;
        grid.clear();
    }

    /**
     * @return the real width of this Grid.
     */
    public int getWidth(){
        return width;
    }

    /**
     * @return the real height of this Grid.
     */
    public int getHeight(){
        return height;
    }

    /**
     * @return the width of the grid in indices.
     */
    public int getWidthI(){
        return width - 1;
    }

    /**
     * @return the height of the grid in indices.
     */
    public int getHeightI(){
        return height - 1;
    }

    public GridCell getContent(int x, int y){
        return grid.get(x).get(y);
    }

    /**
     * A cell in the Grid that contains Tiles. Necessary for instanced rendering as matrices need to be collected.
     * At the moment it can only contain Tiles.
     */
    public static class GridCell {

        private ArrayList<Tile> tiles;
        private HashMap<GameObject, ArrayList<Float>> matrices;

        private ArrayList<Float> intermeditateArray;

        private GridCell(){
            tiles = new ArrayList<>();
            matrices = new HashMap<>();
            intermeditateArray = new ArrayList<>();
        }

        private void addEntity(Tile tile){
            tiles.add(tile);
        }

        /**
         * Creates a HashMap that, for every type of game object, stores all matrices of that type with the type as the key.
         */
        private void cacheMatrices(){
            for (GameObject tileType : GameObject.values()){
                intermeditateArray.clear();
                for (Tile tile : tiles){
                    if (tile.getGameObjectType() == tileType){
                        for (float element : tile.getMatrixArray()){
                            intermeditateArray.add(element);
                        }
                    }
                }
                matrices.put(tileType, new ArrayList<>(intermeditateArray));
            }
        }

        public ArrayList<Tile> getTiles(){
            return tiles;
        }

        public ArrayList<Float> getMatrices(GameObject tileType){
            return matrices.get(tileType);
        }
    }
}
