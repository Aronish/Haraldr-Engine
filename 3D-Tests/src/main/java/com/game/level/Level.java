package com.game.level;

import com.game.Camera;
import com.game.Input;
import com.game.graphics.InstancedRenderer;
import com.game.graphics.Renderer;
import com.game.level.tiles.EnumTiles;
import com.game.math.Vector2f;
import com.game.physics.CollisionDetector;

import java.util.HashSet;

/**
 * Main Level class to house everything contained in one level.
 */
public class Level {

    private World world;
    private Player player;

    private HashSet<Grid.GridCell> visibleGridCells;
    private HashSet<Entity> frustumCulledObjects;

    /**
     * Constructs a Level.
     */
    public Level(){
        player = new Player();
        player.resetPosition();
        world = new World();
        visibleGridCells = new HashSet<>();
        frustumCulledObjects = new HashSet<>();
    }

    /**
     * Process input, update physics/movement, do collisions, update matrices last.
     */
    public void updateLevel(float deltaTime){
        Input.processInput(deltaTime, player, world);
        player.update(deltaTime); //Update Player no matter what.
        checkVisibleGridCells();
        doCollisions();
        updateMatrices();
    }

    /**
     * Renders the game objects in this Level.
     */
    public void renderLevel(){
        if (Input.usingInstancedRendering()){
            for (Grid.GridCell gridCell : visibleGridCells){
                /*
                for (EnumTiles tileType : EnumTiles.values()){
                    if (Input.usingLimitedInstancing()){
                        InstancedRenderer.renderInstancedLimited(gridCell.getTileMatrices(tileType), tileType);
                    }else{
                        InstancedRenderer.renderInstanced(gridCell.getTileContinuousArray(tileType), tileType);
                    }

                    //InstancedRenderer.renderInstancedLimited(gridCell.getTileMatrices(tileType), tileType);
                    //InstancedRenderer.renderInstanced(gridCell.getMatrices(), tileType);
                }
                */
                InstancedRenderer.renderInstanced(gridCell);
            }
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
                    for (Entity entity : grid.getContent((int) playerGridPosition.getX() + x, (int) playerGridPosition.getY() + y).getTiles()){
                        Camera.isInView(frustumCulledObjects, entity);
                    }
                }
            }
        }
    }

    /**
     * Performs orthographic frustum culling on the visible GridCells. Only used with instanced rendering.
     */
    private void frustumCull(){
        for (Grid.GridCell gridCell : visibleGridCells){
            for (Entity entity : gridCell.getTiles()){
                Camera.isInView(frustumCulledObjects, entity);
            }
        }
    }

    /**
     * Updates all the matrices.
     */
    private void updateMatrices(){
        player.updateMatrix();
        if (Input.usingInstancedRendering()){ //TODO: What is this supposed to be??
            for (Grid.GridCell gridCell : visibleGridCells){
                gridCell.updateMatrices();
            }
        }else{
            frustumCulledObjects.forEach(Entity::updateMatrix);
        }
    }

    /**
     * Does collision detection and resolution for the objects requiring collisions.
     */
    private void doCollisions() {
        for (Entity entity : frustumCulledObjects){
            if (!(entity instanceof IBackground)){
                CollisionDetector.doCollisions(entity, entity.getTexturedModels().get(0), player);
            }
        }
    }

    /**
     * Deletes all shader programs, buffer objects and textures.
     */
    public void cleanUp(){
        world.cleanUp();
        player.cleanUp();
    }
}