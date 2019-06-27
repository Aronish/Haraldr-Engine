package main.java.graphics;

import main.java.level.Entity;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

/**
 * Handles rendering of game objects.
 */
public class Renderer {

    private static final Shader SQUARE_SHADER = new Shader("src/main/java/shaders/square_shader");

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
     * Prepares and renders an Entity. Don't bother to unbind everything.
     * @param entity the entity to render.
     */
    public static void render(Entity entity){
        for (TexturedModel texturedModel : entity.getTexturedModels()){
            SQUARE_SHADER.use();
            SQUARE_SHADER.setMatrix(entity.getMatrixArray());
            texturedModel.getVertexArray().bind();
            texturedModel.getTexture().bind();
            texturedModel.getVertexArray().draw();
        }
    }

    /**
     * Deletes the shaders.
     */
    public static void deleteShaders(){
        SQUARE_SHADER.delete();
    }
}
