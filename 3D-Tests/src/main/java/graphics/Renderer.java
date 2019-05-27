package main.java.graphics;

import main.java.level.Entity;
import main.java.debug.Line;

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
