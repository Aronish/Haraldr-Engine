package main.java.level;

import main.java.Input;
import main.java.debug.DebugLines;
import main.java.graphics.Renderer;
import main.java.graphics.TexturedModel;
import main.java.math.Vector3f;
import main.java.physics.CollisionDetector;

import java.util.ArrayList;

/**
 * Main Level class to house everything contained in one level. (Should really only have one World Object)...
 */
public class Level {

    //TODO ArrayLists for Entities and MovableEntities that require deltaTime.
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
            this.obstacles.add(new Obstacle(new Vector3f(i + 1, i + 1)));
        }
        this.debugLines = new DebugLines();
        this.debugLines.addDebugLines(this.world);
        for (Obstacle obstacle : this.obstacles){
            this.debugLines.addDebugLines(obstacle);
        }
    }

    /**
     * Updates the matrices of the game objects in this Level.
     */
    public void updateLevel(float deltaTime){
        Input.processInput(deltaTime, this.player);
        doCollisions();
        if (Input.isDebugEnabled()){
            updateDebug();
        }
        this.world.updateMatrix();
        for (Obstacle obstacle : this.obstacles){
            obstacle.updateMatrix();
        }
        this.player.update(deltaTime);
    }

    private void updateDebug(){
        this.debugLines.setPlayerEnd(this.player.getPosition());
        this.debugLines.update();
    }

    /**
     * Renders the game objects in this Level.
     */
    public void renderLevel(){
        Renderer.render(this.world);
        for (Obstacle obstacle : this.obstacles){
            Renderer.render(obstacle);
        }
        if (Input.isDebugEnabled()){
            this.debugLines.render();
        }
        Renderer.render(this.player);
    }

    /**
     * Does collision detection and resolution for the objects requiring collisions.
     */
    private void doCollisions() {
        for (int texMod = 0; texMod < this.world.getTexturedModels().size(); texMod++) {
            TexturedModel texturedModel = this.world.getTexturedModels().get(texMod);
            if (CollisionDetector.checkCollision(this.world, texturedModel, this.player)) {
                CollisionDetector.doCollision(CollisionDetector.getCollisionDirection(this.world, texturedModel, this.player), this.player);
            }
        }
        for (Obstacle obstacle : this.obstacles){
            TexturedModel texturedModel = obstacle.getTexturedModels().get(0);
            if (CollisionDetector.checkCollision(obstacle, texturedModel, this.player)){
                CollisionDetector.doCollision(CollisionDetector.getCollisionDirection(obstacle, texturedModel, this.player), this.player);
            }
        }
    }
}