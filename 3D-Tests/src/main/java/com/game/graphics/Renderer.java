package com.game.graphics;

import com.game.Camera;
import com.game.level.Entity;
import com.game.math.Matrix4f;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

/**
 * Handles rendering of game objects.
 */
public class Renderer {

    private static final Shader SHADER = new Shader("shaders/shader");

    /**
     * Clears the framebuffer for the next render. Clear color is set in Main#init ATM.
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
     * Prepares and renders an Entity. Don't bother to unbind everything.
     * @param entity the entity to render.
     */
    public static void render(Entity entity){
        Models.SPRITE_SHEET.bind();
        SHADER.use();
        SHADER.setMatrix(Camera.viewMatrix.matrix, "view");
        SHADER.setMatrix(Matrix4f._orthographic.matrix, "projection");
        for (TexturedModel texturedModel : entity.getTexturedModels()){
            SHADER.setMatrix(entity.getMatrixArray(), "matrix");
            texturedModel.getVertexArray().bind();
            texturedModel.getVertexArray().draw();
        }
    }

    /**
     * Deletes the shaders.
     */
    public static void deleteShaders(){
        SHADER.delete();
    }
}
