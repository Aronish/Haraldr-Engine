package main.java.level;

import main.java.Camera;
import main.java.Input;
import main.java.debug.DebugLines;
import main.java.graphics.Renderer;
import main.java.math.Vector3f;
import main.java.physics.CollisionDetector;

import java.util.HashSet;

/**
 * Main Level class to house everything contained in one level. (Should really only have one World Object)...
 */
public class Level {

    private World world;
    private Player player;
    private DebugLines debugLines;

    private HashSet<Entity> visibleObjects;

    /**
     * Constructs a Level.
     */
    public Level(){
        this.player = new Player();
        this.player.resetPosition();
        this.world = new World();
        this.debugLines = new DebugLines();
        this.visibleObjects = new HashSet<>();
    }

    /**
     * Process input, update physics/movement, do collisions, update matrices last.
     */
    public void updateLevel(float deltaTime){
        Input.processInput(deltaTime, this.player, this.world);
        this.player.update(deltaTime); //Update Player no matter what.
        checkVisibility();
        doCollisions();
        if (Input.isDebugEnabled()){
            this.debugLines.setPlayerEnd(this.player.getPosition());
        }
        updateMatrices();
    }

    private void checkVisibility(){
        this.visibleObjects.clear();
        for (WorldTile worldTile : this.world.getWorldTiles()){
            for (Entity tile : worldTile.getTiles()){
                Camera.isInView(this.visibleObjects, tile);
            }
        }
    }

    /**
     * Updates all the matrices.
     */
    private void updateMatrices(){
        this.debugLines.update();
        this.player.updateMatrix();
        this.visibleObjects.forEach(Entity::updateMatrix);
    }

    /**
     * Renders the game objects in this Level.
     */
    public void renderLevel(){
        if (Input.isDebugEnabled()){
            this.debugLines.render();
        }
        this.visibleObjects.forEach(Renderer::render);
        Renderer.render(this.player);
    }

    /**
     * Does collision detection and resolution for the objects requiring collisions.
     */
    private void doCollisions() {
        for (WorldTile worldTile : this.world.getWorldTiles()){
            if (CollisionDetector.checkCollision(worldTile, this.player)){
                for (Entity tile : worldTile.getTiles()){
                    if (!(tile instanceof Tree)){
                        CollisionDetector.doCollisions(tile, tile.getTexturedModels().get(0), this.player);
                    }
                }
            }
        }
    }

    /**
     * Deletes all shader programs, buffer objects and textures.
     */
    public void cleanUp(){
        this.world.cleanUp();
        this.player.cleanUp();
        this.debugLines.cleanUp();
    }
}