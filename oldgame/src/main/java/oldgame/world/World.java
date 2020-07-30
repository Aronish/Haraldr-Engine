package oldgame.world;

import oldgame.gameobject.GameObject;
import oldgame.gameobject.tile.Tile;
import oldgame.gameobject.tile.TileFactory;
import oldgame.gameobject.tile.TileGrassTuft;
import oldgame.gameobject.tile.TileTree;
import engine.math.SimplexNoise;
import engine.math.Vector2f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World
{
    private static final TileFactory TILE_FACTORY = new TileFactory();
    private static final Random RANDOM = new Random();
    private static final int WORLD_WIDTH = 1000;
    private static final int WORLD_HEIGHT = 150;

    private static double noiseScale = 0.042d;
    private List<Tile> tiles = new ArrayList<>();
    private Grid grid = new Grid();

    public World()
    {
        generateWorld();
    }

    /**
     * Generates a list of WorldTiles with varying heights based on SimplexNoise with a random seed.
     */
    public void generateWorld()
    {
        tiles = new ArrayList<>();
        grid = new Grid();
        double seed = RANDOM.nextDouble() * 100000.0d;
        for (int i = 0; i < WORLD_WIDTH; ++i)
        {
            int y = (int) ((SimplexNoise.noise(i * noiseScale, 0.0d, seed) + 1.0d) / 2.0d * 60.0d);
            fillColumn(new Vector3f(i, y + WORLD_HEIGHT, 0f));
        }
        grid.populateGrid(tiles);
        tiles = null;
    }

    /**
     * Fills a one block wide column with different blocks according to the generation algorithm.
     * @param position the position of the initial block.
     */
    private void fillColumn(@NotNull Vector3f position)
    {
        if (position.getY() < 58.0f + WORLD_HEIGHT && RANDOM.nextInt() % 3 == 0)
        {
            tiles.add(new TileTree(Vector3f.add(position, new Vector3f(0.0f, 3.0f, 0f))));
        }else
        {
            tiles.add(new TileGrassTuft(Vector3f.add(position, new Vector3f(0.0f, 0.25f, 0f))));
        }
        Tile topTile = TILE_FACTORY.createTile(position, position.getY() > 45.0f + WORLD_HEIGHT ? GameObject.GRASS_SNOW : GameObject.GRASS);
        if (RANDOM.nextBoolean())
        {
            topTile.setScale(new Vector2f(-1.0f, 1.0f));
        }
        tiles.add(topTile);
        {
            int counter = 0;
            for (float i = position.addY(-1f).getY(); i >= 0.0f; --i, ++counter)
            {
                tiles.add(TILE_FACTORY.createTile(new Vector3f(position.getX(), i, 0f), i < 40.0f + WORLD_HEIGHT ? (counter > 16 ? GameObject.STONE : GameObject.DIRT) : (counter > 24 ? GameObject.STONE : GameObject.DIRT)));
            }
        }
    }

    public void increaseNoiseScale(double dScale)
    {
        noiseScale += dScale;
    }

    public void resetNoiseScale()
    {
        noiseScale = 0.042d;
    }

    public Grid getGrid()
    {
        return grid;
    }
}