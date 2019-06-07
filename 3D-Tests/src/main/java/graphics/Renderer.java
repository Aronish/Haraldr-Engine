package main.java.graphics;

import main.java.level.Entity;
import main.java.debug.Line;
import main.java.level.Tile;
import main.java.level.WorldTile;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

/**
 * Handles rendering of game objects.
 */
public class Renderer {

    /**
     * Clears the buffer for the next render. Clear color is set in Main#init ATM.
     */
    public static void clear(){
        glClear(GL_COLOR_BUFFER_BIT);
    }

    /**
     * Sets the clear color to the rgb color specified by the arguments. [0-1]
     * @param r the red channel.
     * @param g the green channel.
     * @param b the blue channel.
     * @param a the alpha channel.
     */
    public static void setClearColor(float r, float g, float b, float a){
        glClearColor(r, g, b, a);
    }

    /**
     * Prepares and renders an Entity.
     * @param entity the entity to render.
     */
    public static void render(Entity entity){
        for (int texMod = 0; texMod < entity.getTexturedModels().size(); texMod++){
            entity.getTexturedModels().get(texMod).getShader().use();
            entity.setMatrixLocation(texMod);
            entity.setUniformMatrix();
            entity.getTexturedModels().get(texMod).getVertexArray().bind();
            entity.getTexturedModels().get(texMod).getTexture().bind();
            entity.getTexturedModels().get(texMod).getVertexArray().draw();
            entity.getTexturedModels().get(texMod).getTexture().unbind();
            entity.getTexturedModels().get(texMod).getVertexArray().unbind();
            entity.getTexturedModels().get(texMod).getShader().unuse();
        }
    }

    public static void render(WorldTile worldTile){
        for (Tile tile : worldTile.getTiles()){
            render(tile);
        }
    }

    /**
     * Prepares and renders a line.
     * @param line the line to render.
     */
    public static void render(Line line){
        line.getShader().use();
        line.setMatrixLocation();
        line.setUniformMatrix();
        line.getVertexArray().bind();
        line.getVertexArray().draw();
        line.getVertexArray().unbind();
        line.getShader().unuse();
    }
}
