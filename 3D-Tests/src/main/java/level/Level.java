package main.java.level;

import main.java.Input;
import main.java.debug.DebugLines;
import main.java.graphics.Renderer;
import main.java.math.Vector3f;
import main.java.physics.CollisionDetector;

/**
 * Main Level class to house everything contained in one level. (Should really only have one World Object)...
 */
public class Level {

    private World world;
    private Player player;
    private Tile startingTile;
    private DebugLines debugLines;

    /**
     * Constructs a Level with the base elements like a Player and World.
     */
    public Level(){
        this.player = new Player(new Vector3f(0.0f, 25.0f), 2.0f);
        this.startingTile = new Tile(new Vector3f(0.0f, 23.0f));
        this.world = new World();
        this.debugLines = new DebugLines();
    }

    /**
     * Process input, update physics/movement, do collisions, update matrices last.
     */
    public void updateLevel(float deltaTime){
        Input.processInput(deltaTime, this.player, this.world);
        this.player.update(deltaTime);
        doCollisions();
        if (Input.isDebugEnabled()){
            this.debugLines.setPlayerEnd(this.player.getPosition());
        }
        updateMatrices();
    }

    private void updateMatrices(){
        this.debugLines.update();
        this.world.updateMatrix();
        for (WorldTile worldTile : this.world.getWorldTiles()){
            worldTile.updateMatrix();
        }
        this.startingTile.updateMatrix();
        this.player.updateMatrix();
    }

    /**
     * Renders the game objects in this Level.
     */
    public void renderLevel(){
        if (Input.isDebugEnabled()){
            this.debugLines.render();
        }
        Renderer.render(this.world);
        for (WorldTile worldTile : this.world.getWorldTiles()){
            Renderer.render(worldTile);
        }
        Renderer.render(this.startingTile);
        Renderer.render(this.player);
    }

    /**
     * Does collision detection and resolution for the objects requiring collisions.
     */
    private void doCollisions() {
        CollisionDetector.doCollisions(this.startingTile, this.startingTile.getTexturedModels().get(0), this.player);
        for (WorldTile worldTile : this.world.getWorldTiles()){
            if (CollisionDetector.checkCollision(worldTile, this.player)){
                for (Tile tile : worldTile.getTiles()){
                    CollisionDetector.doCollisions(tile, tile.getTexturedModels().get(0), this.player);
                }
            }
        }
    }
}