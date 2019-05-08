package main.java.level;

import main.java.graphics.Renderer;
import main.java.math.Vector3f;

/**
 * Main Level class to house everything contained in one level. (Should only have one world Object)...
 */
public class Level {

    private World world;
    private World world2;
    private Player player;

    public Level(){
        this.player = new Player();
        this.world = new World(this.player.getPosition());
        this.world2 = new World(new Vector3f(30.0f, 10.0f), this.player.getPosition());
    }

    public void updateLevel(){
        this.world.updateMatrix(this.player.getPosition());
        this.world2.updateMatrix(this.player.getPosition());
        this.player.updateMatrix();
    }

    public void renderLevel(){
        Renderer.render(this.world);
        Renderer.render(this.world2);
        Renderer.render(this.player);
    }

    public World getWorld(){
        return this.world;
    }

    public Player getPlayer(){
        return this.player;
    }
}
