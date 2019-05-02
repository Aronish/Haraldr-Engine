package main.java.graphics;

import main.java.Entity;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class Renderer {

    /**
     * Clears the buffer for the next render. Clear color is set in Main#init ATM.
     */
    public static void clear(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
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
}
