package com.game.level;

import com.game.debug.Logger;
import com.game.level.tiles.EnumTiles;
import com.game.level.tiles.Tile;
import com.game.math.Matrix4f;

import java.util.ArrayList;

import static com.game.Main.fastFloor;

/**
 * Contains all objects int the World in a grid system.
 */
public class Grid {

    public static final int GRID_SIZE = 16; //Apparently, this cannot be larger due to GLSL limitations. TO BE FIXED! OR NOT!
    public static final int CONT_MAT4_ARRAY_LENGTH = (int) Math.pow(GRID_SIZE, 2) * 16;
    private static final int CONT_VEC2_ARRAY_LENGTH = (int) Math.pow(GRID_SIZE, 2) * 8;

    private int width, height;

    /**
     * List of lists on the x axis which contain lists of tiles.
     */
    private ArrayList<ArrayList<GridCell>> grid;

    /**
     * Initializes the grid lists and sets width and height to 0.
     */
    Grid(){
        width = 0;
        height = 0;
        grid = new ArrayList<>();
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
            grid.get(x).get(y).addEntity(entity);
            succeeded = true;
        }
    }

    /**
     * Clears the whole grid and resets its dimensions.
     */
    void clear(){
        width = 0;
        height = 0;
        grid.clear();
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    /**
     * Gets the width of the grid in indices.
     * @return the width of the grid in indices.
     */
    int getWidthI(){
        return width - 1;
    }

    /**
     * Gets the height of the grid in indices.
     * @return the height of the grid in indices.
     */
    int getHeightI(){
        return height - 1;
    }

    /**
     * Gets the GridCell at the specified grid coordinates.
     * @param x the x grid coordinate.
     * @param y the y grid coordinate.
     * @return the GridCell.
     */
    public GridCell getContent(int x, int y){
        return grid.get(x).get(y);
    }

    /**
     * A cell in the Grid that contains Entities. Necessary for instanced rendering as matrices need to be collected according to the type of Entities.
     * At the moment it can only contain Tiles.
     */
    public class GridCell {

        private ArrayList<Tile> tiles;

        private float[] matrices;
        private int matrixInsertIndex;

        private float[] vertices;
        private int vertexInsertIndex;

        private float[] textureCoordinates;
        private int texCoordInsertIndex;

        GridCell(){
            tiles = new ArrayList<>();
            matrices = new float[CONT_MAT4_ARRAY_LENGTH];
            matrixInsertIndex = 0;
            vertices = new float[CONT_VEC2_ARRAY_LENGTH];
            vertexInsertIndex = 0;
            textureCoordinates = new float[CONT_VEC2_ARRAY_LENGTH];
            texCoordInsertIndex = 0;
        }

        /**
         * Adds a entity to this GridCell.
         * @param entity the Entity to add.
         */
        private void addEntity(Entity entity){
            if (entity instanceof Tile){
                tiles.add((Tile) entity);
                float[] vertices = ((Tile) entity).getTileType().texturedModel.getVertexArray().getVertices();
                for (float vertex : vertices){
                    this.vertices[vertexInsertIndex] = vertex;
                    ++vertexInsertIndex;
                }
                float[] textureCoordinates = ((Tile) entity).getTileType().texturedModel.getVertexArray().getTextureCoordinates();
                for (float texcoord : textureCoordinates){
                    this.textureCoordinates[texCoordInsertIndex] = texcoord;
                    ++texCoordInsertIndex;
                }
                float[] matrix = entity.getMatrix().matrix;
                for (float element : matrix){
                    matrices[matrixInsertIndex] = element;
                    ++matrixInsertIndex;
                }
            }else{
                Logger.setErrorLevel();
                Logger.log("Grid doesn't support any other Entities than Tiles!");
            }
        }

        /**
         * Updates the matrices for all the Entities in this GridCell.
         */
        void updateMatrices(){
            tiles.forEach(Tile::updateMatrix);
            updateMatrixArray();
        }

        void updateMatrixArray(){
            matrices = new float[CONT_MAT4_ARRAY_LENGTH];
            matrixInsertIndex = 0;
            for (Tile tile : tiles){
                float[] matrix = tile.getMatrix().matrix;
                for (float element : matrix){
                    matrices[matrixInsertIndex] = element;
                    ++matrixInsertIndex;
                }
            }
        }

        /**
         * @return all tiles in this GridCell.
         */
        public ArrayList<Tile> getTiles(){
            return tiles;
        }

        /**
         * @param tileType the type of Tiles whose matrices will be collected.
         * @return all the matrices of all the Tiles of the specified Type.
         */
        ArrayList<Matrix4f> getTileMatrices(EnumTiles tileType){
            ArrayList<Matrix4f> matrices = new ArrayList<>();
            tiles.forEach(tile -> {
                if (tile.getTileType() == tileType){
                    matrices.add(tile.getMatrix());
                }
            });
            return matrices;
        }

        public int getCount(){
            return tiles.size();
        }

        public float[] getVertices(){
            return vertices;
        }

        public float[] getTextureCoordinates(){
            return textureCoordinates;
        }

        public float[] getMatrices(){
            return matrices;
        }
    }
}
