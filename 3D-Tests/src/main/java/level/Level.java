package main.java.level;

import main.java.Camera;
import main.java.Input;
import main.java.debug.Logger;
import main.java.graphics.Renderer;
import main.java.math.Vector2f;
import main.java.math.Vector3f;
import main.java.physics.CollisionDetector;

import java.util.ArrayList;
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
        Vector2f playerGridPosition = this.world.getGrid().getPlayerGridPos(this.player.getPosition());
        this.visibleObjects.clear();
        for (int x = -2; x <= 2; ++x){
            if (playerGridPosition.getX() + x < 0){
                continue;
            }
            for (int y = -2; y <= 2; ++y){
                if (playerGridPosition.getY() + y < 0){
                    continue;
                }
                ArrayList<Entity> entities = this.world.getGrid().getContent((int) playerGridPosition.getX() + x, (int) playerGridPosition.getY() + y);
                if (entities != null){
                    for (Entity entity : entities){
                        Camera.isInView(this.visibleObjects, entity);
                    }
                }
            }
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