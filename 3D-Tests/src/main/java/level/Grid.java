package main.java.level;

import main.java.debug.Logger;
import main.java.level.tiles.Tile;
import main.java.level.tiles.TileDirt;
import main.java.level.tiles.TileGrass;
import main.java.level.tiles.TileGrassSnow;
import main.java.level.tiles.TileStone;
import main.java.math.Matrix4f;
import main.java.math.Vector2f;
import main.java.math.Vector3f;

import java.util.ArrayList;

import static main.java.Main.fastFloor;

/**
 * Contains all objects int the World in a grid system.
 */
class Grid {

    private static final int GRID_SIZE = 16;

    private int width, height;

    /**
     * List of lists on the x axis which contain lists of entities.
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
     * Populates the grid with the supplied entities.
     * @param entities the entities to be put in the grid.
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
        if (entity.getMatrix() == null){
            Logger.setErrorLevel();
            Logger.log("IT's Fucking Null");
        }
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
     * Calculates in which grid the Player is.
     * @param playerPosition the position of the Player.
     * @return the grid coordinates.
     */
    Vector2f getPlayerGridPosition(Vector3f playerPosition){
        return new Vector2f((float) fastFloor(playerPosition.getX() / GRID_SIZE), (float) fastFloor(playerPosition.getY() / GRID_SIZE));
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
     * Gets the list of entities in the grid cell att grid coordinates (x, y).
     * @param x the x grid coordinate.
     * @param y the y grid coordinate.
     * @return the list of entities. Can return null but shouldn't.
     */
    /*ArrayList<Entity> getContent(int x, int y){
        try{
            return this.grid.get(x).get(y);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return null;
    }
    */

    GridCell getContent(int x, int y){
        return this.grid.get(x).get(y);
    }

    class GridCell {

        private ArrayList<Entity> grass;
        private ArrayList<Entity> dirt;
        private ArrayList<Entity> snow;
        private ArrayList<Entity> stone;

        private ArrayList<Matrix4f> grassMatrices;
        private ArrayList<Matrix4f> dirtMatrices;
        private ArrayList<Matrix4f> snowMatrices;
        private ArrayList<Matrix4f> stoneMatrices;

        GridCell(){
            this.grass = new ArrayList<>();
            this.dirt = new ArrayList<>();
            this.snow = new ArrayList<>();
            this.stone = new ArrayList<>();

            this.grassMatrices = new ArrayList<>();
            this.dirtMatrices = new ArrayList<>();
            this.snowMatrices = new ArrayList<>();
            this.stoneMatrices = new ArrayList<>();
        }

        private void addEntity(Entity entity){
            if (entity instanceof TileGrass){
                this.grass.add(entity);
                this.grassMatrices.add(entity.getMatrix());
            }else if (entity instanceof TileDirt){
                this.dirt.add(entity);
                this.dirtMatrices.add(entity.getMatrix());
            }else if (entity instanceof TileGrassSnow){
                this.snow.add(entity);
                this.snowMatrices.add(entity.getMatrix());
            }else if (entity instanceof TileStone){
                this.stone.add(entity);
                this.stoneMatrices.add(entity.getMatrix());
            }
        }

        private void updateMatrixArrays(){
            this.grassMatrices.clear();
            this.dirtMatrices.clear();
            this.snowMatrices.clear();
            this.stoneMatrices.clear();

            this.grass.forEach(grass -> this.grassMatrices.add(grass.getMatrix()));
            this.dirt.forEach(dirt -> this.grassMatrices.add(dirt.getMatrix()));
            this.snow.forEach(snow -> this.grassMatrices.add(snow.getMatrix()));
            this.stone.forEach(stone -> this.grassMatrices.add(stone.getMatrix()));
        }

        void updateMatrices(){
            this.grass.forEach(Entity::updateMatrix);
            this.dirt.forEach(Entity::updateMatrix);
            this.snow.forEach(Entity::updateMatrix);
            this.stone.forEach(Entity::updateMatrix);
            updateMatrixArrays();
        }

        public ArrayList<Matrix4f> getGrassMatrices() {
            return this.grassMatrices;
        }

        public ArrayList<Matrix4f> getDirtMatrices() {
            return this.dirtMatrices;
        }

        public ArrayList<Matrix4f> getSnowMatrices() {
            return this.snowMatrices;
        }

        public ArrayList<Matrix4f> getStoneMatrices() {
            return this.stoneMatrices;
        }
    }
}
