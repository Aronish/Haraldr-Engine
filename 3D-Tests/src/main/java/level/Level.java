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
        this.player = new Player(new Vector3f(0.0f, 50.0f));
        this.startingTile = new Tile(new Vector3f(0.0f, 47.0f));
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
        this.world.getWorldTiles().forEach(WorldTile::updateMatrix);
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
        this.world.getWorldTiles().forEach(Renderer::render);
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

    public void cleanUp(){
        this.world.cleanUp();
        this.startingTile.cleanUp();
        this.player.cleanUp();
        this.debugLines.cleanUp();
    }
}