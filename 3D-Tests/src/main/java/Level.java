package main.java;

import main.java.graphics.Renderer;

/**
 * Main Level class to house everything contained in one level. (Should only have one world Object)...
 */
class Level {

    private World world;
    private Player player;

    Level(){
        this.world = new World();
        this.player = new Player();
    }

    void updateLevel(){
        this.world.updateMatrix();
        this.player.updateMatrix();
    }

    void renderLevel(){
        Renderer.render(this.world);
        Renderer.render(this.player);
    }

    World getWorld(){
        return this.world;
    }

    Player getPlayer(){
        return this.player;
    }
}
