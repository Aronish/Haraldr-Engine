package main.java.level;
//TODO Should probably have a grid system for efficient collision detection later.
import main.java.Camera;
import main.java.Input;
import main.java.graphics.Renderer;

/**
 * Main Level class to house everything contained in one level. (Should really only have one World Object)...
 */
public class Level {

    private World world;
    private Player player;

    /**
     * Constructs a Level with the base elements like a Player and World.
     */
    public Level(){
        this.player = new Player();
        this.world = new World(this.player.getPosition());
    }

    /**
     * Updates the matrices of the game objects in this Level.
     */
    public void updateLevel(double deltaTime){
        this.world.updateMatrix(this.player.getPosition());
        Camera.setPosition(this.player.getPosition());
        this.player.updateMatrix(deltaTime);
    }

    /**
     * Renders the game objects in this Level.
     */
    public void renderLevel(){
        Renderer.render(this.world);
        if (Input.shouldRenderDebug()){
            Renderer.renderDebugLines(this.world);
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
}
