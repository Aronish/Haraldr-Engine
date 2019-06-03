package main.java.level;

import main.java.Input;
import main.java.debug.DebugLines;
import main.java.graphics.Renderer;
import main.java.math.Vector3f;
import main.java.physics.CollisionDetector;

import java.util.ArrayList;

/**
 * Main Level class to house everything contained in one level. (Should really only have one World Object)...
 */
public class Level {

    private World world;
    private Player player;
    private DebugLines debugLines;
    private ArrayList<Obstacle> obstacles;

    /**
     * Constructs a Level with the base elements like a Player and World.
     */
    public Level(){
        this.player = new Player(new Vector3f(0.0f, 0.0f));
        this.world = new World();
        this.obstacles = new ArrayList<>();
        for (int i = 0; i < 6; i++){
            this.obstacles.add(new Obstacle(new Vector3f(i + 1.0f, i + 1.0f)));
        }
        this.obstacles.add(new Obstacle(new Vector3f(7.0f, 6.0f)));
        this.debugLines = new DebugLines();
        this.debugLines.addDebugLines(this.world);
        for (Obstacle obstacle : this.obstacles){
            this.debugLines.addDebugLines(obstacle);
        }
    }

    /**
     * Process input, update physics/movement, do collisions, update matrices last.
     */
    public void updateLevel(float deltaTime){
        Input.processInput(deltaTime, this.player);
        this.player.update(deltaTime);
        doCollisions();
        if (Input.isDebugEnabled()){
            this.debugLines.setPlayerEnd(this.player.getPosition());
        }
        updateMatrices();
    }

    private void updateMatrices(){
        this.debugLines.update();
        for (Obstacle obstacle : this.obstacles){
            obstacle.updateMatrix();
        }
        this.world.updateMatrix();
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
        for (Obstacle obstacle : this.obstacles){
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
        for (Obstacle obstacle : this.obstacles){
            CollisionDetector.doCollisions(obstacle, obstacle.getTexturedModels().get(0), this.player);
        }
    }
}