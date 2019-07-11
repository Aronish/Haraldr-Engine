package main.java.graphics;

import main.java.level.tiles.EnumTiles;
import main.java.math.Matrix4f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

/**
 * Renderer that uses instancing rather than a new draw call for every Entity.
 */
public class InstancedRenderer {

    private static final Shader SQUARE_SHADER_INSTANCED = new Shader("src/main/java/shaders/square_shader_instanced");

    /**
     * Clears the framebuffer for the next render. Clear color is set in Main#init ATM.
     */
    public static void clear(){
        glClear(GL_COLOR_BUFFER_BIT);
    }

    /**
     * Renders a batch of Tiles of the same type. Max 256 ATM.
     * @param matrices the matrices of the Tiles.
     * @param tileType the type of Tile to render.
     */
    public static void renderInstanced(ArrayList<Matrix4f> matrices, EnumTiles tileType){
        SQUARE_SHADER_INSTANCED.use();
        SQUARE_SHADER_INSTANCED.setMatrixArray(matrices);
        tileType.texturedModel.getVertexArray().bind();
        tileType.texturedModel.getTexture().bind();
        tileType.texturedModel.getVertexArray().drawInstanced(matrices.size());
    }

    /**
     * Deletes the shaders.
     */
    public static void deleteShaders(){
        SQUARE_SHADER_INSTANCED.delete();
    }
}
