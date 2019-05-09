package main.java.level;

import main.java.Input;
import main.java.graphics.Renderer;
import main.java.math.Vector3f;

/**
 * Main Level class to house everything contained in one level. (Should really only have one World Object)...
 */
public class Level {

    private World world;
    private World world2;
    private Player player;

    /**
     * Constructs a Level with the base elements like a Player and World.
     */
    public Level(){
        this.player = new Player();
        this.world = new World(this.player.getPosition());
        this.world2 = new World(new Vector3f(30.0f, 10.0f), this.player.getPosition());
    }

    /**
     * Updates the matrices of the game objects in this Level.
     */
    public void updateLevel(){
        this.world.updateMatrix(this.player.getPosition());
        this.world2.updateMatrix(this.player.getPosition());
        this.player.updateMatrix();
    }

    /**
     * Renders the game objects in this Level.
     */
    public void renderLevel(){
        Renderer.render(this.world);
        Renderer.render(this.world2);
        if (Input.shouldRenderDebug()){
            Renderer.renderDebugLines(this.world);
            Renderer.renderDebugLines(this.world2);
        }
        Renderer.render(this.player);
    }

    /**
     * Gets the World in this Level.
     * @return the World object.
     */
    public World getWorld(){
        return this.world;
    }

    /**
     * Gets the Player in this Level.
     * @return the Player.
     */
    public Player getPlayer(){
        return this.player;
    }

    //TEMP
    public World[] getWorlds(){
        return new World[] {this.world, this.world2};
    }
}
