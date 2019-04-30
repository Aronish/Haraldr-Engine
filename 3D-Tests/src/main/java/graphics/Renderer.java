package main.java.graphics;

import main.java.Entity;

public class Renderer {

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
