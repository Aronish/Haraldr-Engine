package main.java.level;

import main.java.Camera;
import main.java.Input;
import main.java.graphics.Renderer;
import main.java.physics.CollisionDetector;

import java.util.HashSet;

/**
 * Main Level class to house everything contained in one level.
 */
public class Level {

    private World world;
    private Player player;

    private HashSet<Entity> visibleObjects;

    /**
     * Constructs a Level.
     */
    public Level(){
        this.player = new Player();
        this.player.resetPosition();
        this.world = new World();
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
        updateMatrices();
    }

    private void checkVisibility(){
        this.visibleObjects.clear();
        for (Entity entity : world.getTiles()){
            Camera.isInView(this.visibleObjects, entity);
        }
    }

    /**
     * Updates all the matrices.
     */
    private void updateMatrices(){
        this.player.updateMatrix();
        this.visibleObjects.forEach(Entity::updateMatrix);
    }

    /**
     * Renders the game objects in this Level.
     */
    public void renderLevel(){
        this.visibleObjects.forEach(Renderer::render);
        Renderer.render(this.player);
    }

    /**
     * Does collision detection and resolution for the objects requiring collisions.
     */
    private void doCollisions() {
        for (Entity entity : this.visibleObjects){
            if (!(entity instanceof IBackground)){
                CollisionDetector.doCollisions(entity, entity.getTexturedModels().get(0), this.player);
            }
        }
    }

    /**
     * Deletes all shader programs, buffer objects and textures.
     */
    public void cleanUp(){
        this.world.cleanUp();
        this.player.cleanUp();
    }
}