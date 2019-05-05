package main.java.graphics;

import main.java.Entity;
import main.java.Line;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

/**
 * Handles rendering of Entity's.
 */
public class Renderer {

    /**
     * Clears the buffer for the next render. Clear color is set in Main#init ATM.
     */
    public static void clear(){
        glClear(GL_COLOR_BUFFER_BIT);
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

    public static void render(Line line){
        line.getTexturedModels().get(0).getShader().use();
        line.setMatrixLocation(0);
        line.setUniformMatrix();
        line.getTexturedModels().get(0).getVertexArray().bind();
        line.getTexturedModels().get(0).getVertexArray().draw();
        line.getTexturedModels().get(0).getVertexArray().unbind();
        line.getTexturedModels().get(0).getShader().unuse();
    }
}
