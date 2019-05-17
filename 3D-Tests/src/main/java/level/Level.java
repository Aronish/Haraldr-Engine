package main.java.level;

import main.java.Input;
import main.java.debug.DebugLines;
import main.java.graphics.Renderer;
import main.java.graphics.TexturedModel;
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
        this.player = new Player(new Vector3f(0.0f, -5.0f));
        this.world = new World();
        this.debugLines = new DebugLines();
        this.debugLines.addDebugLines(this.world);
    }

    /**
     * Updates the matrices of the game objects in this Level.
     */
    public void updateLevel(double deltaTime){
        Input.processInput(deltaTime, this.player);
        doCollisions();
        if (Input.isDebugEnabled()){
            this.debugLines.setPlayerEnd(this.player.getPosition());
            this.debugLines.update();
        }
        this.world.updateMatrix();
        this.player.update(deltaTime);
    }

    /**
     * Renders the game objects in this Level.
     */
    public void renderLevel(){
        Renderer.render(this.world);
        if (Input.isDebugEnabled()){
            this.debugLines.render();
        }
        Renderer.render(this.player);
    }

    private void doCollisions() {
        for (int texMod = 0; texMod < this.world.getTexturedModels().size(); texMod++) {
            TexturedModel texturedModel = this.world.getTexturedModels().get(texMod);
            if (CollisionDetector.checkCollision(this.world, texturedModel, this.player)) {
                CollisionDetector.doCollision(CollisionDetector.getCollisionDirection(this.world, texturedModel, this.player), this.player);
            }
        }
    }
}
