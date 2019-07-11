package main.java.level;

import main.java.debug.Logger;
import main.java.level.tiles.EnumTiles;
import main.java.level.tiles.Tile;
import main.java.math.Matrix4f;

import java.util.ArrayList;

import static main.java.Main.fastFloor;

/**
 * Contains all objects int the World in a grid system.
 */
class Grid {

    static final int GRID_SIZE = 16;

    private int width, height;

    /**
     * List of lists on the x axis which contain lists of tiles.
     */
    private ArrayList<ArrayList<GridCell>> grid;

    /**
     * Initializes the grid lists and sets width and height to 0.
     */
    Grid(){
        this.width = 0;
        this.height = 0;
        this.grid = new ArrayList<>();
    }

    /**
     * Populates the grid with the supplied tiles.
     * @param entities the tiles to be put in the grid.
     */
    void populateGrid(ArrayList<Entity> entities){
        entities.forEach(entity -> addEntity(fastFloor(entity.getPosition().getX() / GRID_SIZE), fastFloor(entity.getPosition().getY() / GRID_SIZE), entity));
    }

    /**
     * Adds an entity at the grid coordinate (x, y). Has bounds checking.
     * If x is larger than the current width, another x list is added with the current height.
     * If y is larger than the current height, heights of all x lists are increased by one.
     */
    private void addEntity(int x, int y, Entity entity){
        boolean succeeded = false;
        while (!succeeded){
            if (this.width <= x){
                this.grid.add(new ArrayList<>());
                ArrayList<GridCell> last = this.grid.get(this.grid.size() - 1);
                for (int i = 0; i <= this.height; ++i){
                    last.add(new GridCell());
                }
                ++width;
                continue;
            }
            if (this.height <= y){
                this.grid.forEach(xAxis -> xAxis.add(new GridCell()));
                ++height;
                continue;
            }
            this.grid.get(x).get(y).addEntity(entity);
            succeeded = true;
        }
    }

    /**
     * Clears the whole grid and resets its dimensions.
     */
    void clear(){
        this.width = 0;
        this.height = 0;
        this.grid.clear();
    }

    /**
     * Gets the width of the grid in indices.
     * @return the width of the grid in indices.
     */
    int getWidth(){
        return this.width - 1;
    }

    /**
     * Gets the height of the grid in indices.
     * @return the height of the grid in indices.
     */
    int getHeight(){
        return this.height - 1;
    }

    /**
     * Gets the GridCell at the specified grid coordinates.
     * @param x the x grid coordinate.
     * @param y the y grid coordinate.
     * @return the GridCell.
     */
    GridCell getContent(int x, int y){
        return this.grid.get(x).get(y);
    }

    /**
     * A cell in the Grid that contains Entities. Necessary for instanced rendering as matrices need to be collected according to the type of Entities.
     * At the moment it can only contain Tiles.
     */
    class GridCell {

        private ArrayList<Tile> tiles;

        /**
         * Initializes the ArrayLists.
         */
        GridCell(){
            this.tiles = new ArrayList<>();
        }

        /**
         * Adds a entity to this GridCell.
         * @param entity the Entity to add.
         */
        private void addEntity(Entity entity){
            if (entity instanceof Tile){
                this.tiles.add((Tile) entity);
            }else{
                Logger.setErrorLevel();
                Logger.log("Grid doesn't support any other Entities than Tiles!");
            }
        }

        /**
         * Updates the matrices for all the Entities in this GridCell.
         */
        void updateMatrices(){
            this.tiles.forEach(Tile::updateMatrix);
        }

        /**
         * @return all tiles in this GridCell.
         */
        ArrayList<Tile> getTiles(){
            return this.tiles;
        }

        /**
         * @param tileType the type of Tiles whose matrices will be collected.
         * @return all the matrices of all the Tiles of the specified Type.
         */
        ArrayList<Matrix4f> getTileMatrices(EnumTiles tileType){
            ArrayList<Matrix4f> matrices = new ArrayList<>();
            this.tiles.forEach(tile -> {
                if (tile.getTileType() == tileType){
                    matrices.add(tile.getMatrix());
                }
            });
            return matrices;
        }
    }
}
