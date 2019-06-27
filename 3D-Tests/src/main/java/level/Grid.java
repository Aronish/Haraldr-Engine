package main.java.level;

import main.java.math.Vector2f;
import main.java.math.Vector3f;

import java.util.ArrayList;

import static main.java.Main.fastFloor;

/**
 * Contains all objects int the World in a grid system.
 */
class Grid {

    static final int GRID_SIZE = 16;

    private int width, height;

    /**
     * List of lists on the x axis which contain lists of entities.
     */
    private ArrayList<ArrayList<ArrayList<Entity>>> grid;

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
        boolean succeeded = false;
        while (!succeeded){
            if (this.width <= x){
                this.grid.add(new ArrayList<>());
                ArrayList<ArrayList<Entity>> last = this.grid.get(this.grid.size() - 1);
                for (int i = 0; i <= this.height; ++i){
                    last.add(new ArrayList<>());
                }
                ++width;
                continue;
            }
            if (this.height <= y){
                this.grid.forEach(xAxis -> xAxis.add(new ArrayList<>()));
                ++height;
                continue;
            }
            this.grid.get(x).get(y).add(entity);
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
    ArrayList<Entity> getContent(int x, int y){
        try{
            return this.grid.get(x).get(y);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return null;
    }
}
