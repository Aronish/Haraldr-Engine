package main.java.graphics;

import main.java.Entity;

public class Renderer {

    public void render(Entity entity){
        entity.getTexturedModel().getShader().use();
        entity.setUniformMatrix();
        entity.getTexturedModel().getVertexArray().bind();
        entity.getTexturedModel().getTexture().bind();
        entity.getTexturedModel().getVertexArray().draw();
        entity.getTexturedModel().getTexture().unbind();
        entity.getTexturedModel().getVertexArray().unbind();
        entity.getTexturedModel().getShader().unuse();
    }
}
