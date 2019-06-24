package main.java.level;

import main.java.debug.Logger;
import main.java.math.Vector2f;
import main.java.math.Vector3f;

import java.util.ArrayList;

class Grid {

    private static final int GRID_SIZE = 16;

    /**
     * X -> Y -> Entities in that cell.
     */
    private ArrayList<ArrayList<ArrayList<Entity>>> grid;

    Grid(){
        this.grid = new ArrayList<>();
    }

    void populateGrid(ArrayList<Entity> entities){
        entities.forEach(entity -> addEntity((int) Math.floor(entity.getPosition().getX() / GRID_SIZE), (int) Math.floor(entity.getPosition().getY() / GRID_SIZE), entity));
    }

    private void addEntity(int x, int y, Entity entity){
        boolean succeeded = false;
        while (!succeeded){
            if (this.grid.size() < x + 1){
                this.grid.add(new ArrayList<>());
                continue;
            }
            if (this.grid.get(x).size() < y + 1){
                this.grid.get(x).add(new ArrayList<>());
                continue;
            }
            this.grid.get(x).get(y).add(entity);
            succeeded = true;
        }
    }

    Vector2f getPlayerGridPos(Vector3f playerPosition){
        return new Vector2f((float) Math.floor(playerPosition.getX() / GRID_SIZE), (float) Math.floor(playerPosition.getY() / GRID_SIZE));
    }

    void clear() {
        this.grid.clear();
    }

    ArrayList<Entity> getContent(int x, int y){
        try{
            return this.grid.get(x).get(y);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace(); //TODO: GET RID OF THESE EXCEPTIONS
        }
        return null;
    }

    ArrayList<ArrayList<ArrayList<Entity>>> getGrid(){
        return this.grid;
    }
}
