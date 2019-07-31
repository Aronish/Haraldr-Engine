package com.game.level;

import com.game.Camera;
import com.game.Input;
import com.game.graphics.InstancedRenderer;
import com.game.graphics.Renderer;
import com.game.level.gameobject.tile.Tile;
import com.game.math.Vector2f;
import com.game.physics.CollisionDetector;

import java.util.ArrayList;

/**
 * Main Level class to house everything contained in one level. //TODO Move to Layer system to make the new event system possible.
 */
public class Level {

    private World world;
    private Player player;

    private ArrayList<Grid.GridCell> visibleGridCells;
    private ArrayList<Tile> frustumCulledObjects;

    /**
     * Constructs a Level.
     */
    public Level(){
        player = new Player();
        player.resetPosition();
        world = new World();
        visibleGridCells = new ArrayList<>();
        frustumCulledObjects = new ArrayList<>();
        InstancedRenderer.setupInstancedBuffer();
    }

    /**
     * Process input, update physics/movement, do collisions, update necessary matrices last.
     */
    public void updateLevel(float deltaTime){
        Input.processInput(deltaTime, player, world);
        player.update(deltaTime);
        checkVisibleGridCells();
        doCollisions();
        player.updateMatrix();
    }

    /**
     * Renders the game objects in this Level.
     */
    public void renderLevel(){
        if (Input.usingInstancedRendering()){
            InstancedRenderer.renderGridCells(visibleGridCells);
        }else{
            frustumCulledObjects.forEach(Renderer::render);
        }
        Renderer.render(player);
    }

    /**
     * Main optimization algorithm. Checks chunks in the grid system in a zoom dependent area around the Player and runs them through orthographic frustum culling.
     * Frustum culling alone does wonders, but does not allow for infinite worlds. With the grid system, the world is already loaded in a data structure.
     * Array lookup is far better than looping through every object, since the time required is basically constant.
     * O(1) vs. O(n)
     */
    private void checkVisibleGridCells(){
        Vector2f playerGridPosition = player.getGridPosition();
        Grid grid = world.getGrid();
        visibleGridCells.clear();
        frustumCulledObjects.clear();
        for (int x = -Camera.chunkXRange; x <= Camera.chunkXRange; ++x){
            if (playerGridPosition.getX() + x < 0 || playerGridPosition.getX() + x > grid.getWidthI()){
                continue;
            }
            for (int y = -Camera.chunkYRange; y <= Camera.chunkYRange; ++y){
                if (playerGridPosition.getY() + y < 0 || playerGridPosition.getY() + y > grid.getHeightI()){
                    continue;
                }
                if (Input.usingInstancedRendering()){
                    visibleGridCells.add(grid.getContent((int) playerGridPosition.getX() + x, (int) playerGridPosition.getY() + y)); //TODO: Int cast not good for negative values.
                }else{
                    for (Tile tile : grid.getContent((int) playerGridPosition.getX() + x, (int) playerGridPosition.getY() + y).getTiles()){
                        Camera.isInView(frustumCulledObjects, tile);
                    }
                }
            }
        }
        if (Input.usingInstancedRendering()){
            frustumCull();
        }
    }

    /**
     * Performs orthographic frustum culling on the visible GridCells. Optimization for "legacy rendering", but is, for now, used to determine
     * the object to check collisions against.
     */
    private void frustumCull(){
        for (Grid.GridCell gridCell : visibleGridCells){
            for (Tile tile : gridCell.getTiles()){
                Camera.isInView(frustumCulledObjects, tile);
            }
        }
    }

    /**
     * Does collision detection and resolution for the objects requiring collisions.
     */
    private void doCollisions() {
        for (Tile tile : frustumCulledObjects){
            if (!(tile instanceof IBackground)){
                CollisionDetector.doCollisions(tile, player);
            }
        }
    }
}