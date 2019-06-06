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
    private DebugLines debugLines;

    /**
     * Constructs a Level with the base elements like a Player and World.
     */
    public Level(){
        this.player = new Player(new Vector3f(0.0f, 5.0f));
        this.world = new World();
        this.debugLines = new DebugLines();
        this.debugLines.addDebugLines(this.world);
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
        for (Obstacle obstacle : this.world.getObstacles()){
            obstacle.updateMatrix();
        }
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
        for (Obstacle obstacle : this.world.getObstacles()){
            Renderer.render(obstacle);
        }
        Renderer.render(this.player);
    }

    /**
     * Does collision detection and resolution for the objects requiring collisions.
     */
    private void doCollisions() {
        for (int texMod = 0; texMod < this.world.getTexturedModels().size(); texMod++) {
            CollisionDetector.doCollisions(this.world, this.world.getTexturedModels().get(texMod), this.player);
        }
        for (Obstacle obstacle : this.world.getObstacles()){
            CollisionDetector.doCollisions(obstacle, obstacle.getTexturedModels().get(0), this.player);
        }
    }
}