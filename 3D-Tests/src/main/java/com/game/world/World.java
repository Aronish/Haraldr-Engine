package com.game.world;

import com.game.gameobject.Entity;
import com.game.gameobject.GameObject;
import com.game.gameobject.tile.Tile;
import com.game.gameobject.tile.TileFactory;
import com.game.gameobject.tile.TileTree;
import com.game.math.SimplexNoise;
import com.game.math.Vector2f;
import com.game.math.Vector3f;

import java.util.ArrayList;
import java.util.Random;

/**
 * The main tile world.
 */
public class World {

    private static final TileFactory TILE_FACTORY = new TileFactory();
    private static final Random RANDOM = new Random();
    private static final int WORLD_WIDTH = 1000;
    private static final int WORLD_HEIGHT = 150;

    private static double noiseScale = 0.042d;
    private ArrayList<Tile> tiles;
    private Grid grid;

    public World(){
        tiles = new ArrayList<>();
        grid = new Grid();
        generateWorld();
    }

    /**
     * Generates a list of WorldTiles with varying heights based on SimplexNoise with a RANDOM seed.
     */
    private void generateWorld(){
        double seed = RANDOM.nextDouble() * 100000.0d;
        for (int i = 0; i < WORLD_WIDTH; ++i){
            int y = (int) ((SimplexNoise.noise(i * noiseScale, 0.0d, seed) + 1.0d) / 2.0d * 60.0d);
            fillColumn(new Vector3f(i, y + WORLD_HEIGHT));
        }
        grid.populateGrid(tiles);
    }

    /**
     * Fills a one block wide column with different blocks according to the generation algorithm.
     * @param position the position of the initial block.
     */
    private void fillColumn(Vector3f position){
        if (position.getY() < 58.0f + WORLD_HEIGHT && RANDOM.nextBoolean()){
            tiles.add(new TileTree(position.addReturn(new Vector3f(0.0f, 3.0f))));
        }
        Tile topTile = TILE_FACTORY.createTile(position, position.getY() > 45.0f + WORLD_HEIGHT ? GameObject.GRASS_SNOW : GameObject.GRASS);
        if (RANDOM.nextBoolean()){
            topTile.setScale(new Vector2f(-1.0f, 1.0f));
        }
        tiles.add(topTile);
        {
            int counter = 0;
            for (float i = position.subtractY(1.0f).getY(); i >= 0.0f; --i, ++counter) {
                tiles.add(TILE_FACTORY.createTile(new Vector3f(position.getX(), i), i < 40.0f + WORLD_HEIGHT ? (counter > 16 ? GameObject.STONE : GameObject.DIRT) : (counter > 24 ? GameObject.STONE : GameObject.DIRT)));
            }
        }
    }

    public void regenerateWorld(){
        tiles.clear();
        grid.clear();
        generateWorld();
    }

    public void serialize(){
        tiles.forEach(Entity::serialize);
    }

    public void increaseNoiseScale(double dScale){
        noiseScale += dScale;
    }

    public void resetNoiseScale(){
        noiseScale = 0.042d;
    }

    public Grid getGrid(){
        return grid;
    }
}