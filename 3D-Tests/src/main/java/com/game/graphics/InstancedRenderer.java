package com.game.graphics;

import com.game.Camera;
import com.game.level.Grid;
import com.game.level.tiles.EnumTiles;
import com.game.math.Matrix4f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

/**
 * Renderer that uses instancing rather than a new draw call for every Entity.
 */
public class InstancedRenderer {

    private static final Shader INSTANCED_SHADER = new Shader("shaders/instanced_shader.vert", "shaders/shader.frag");
    private static final Shader INSTANCED_SHADER_2 = new Shader("shaders/instanced_shader_2.vert", "shaders/shader.frag");

    private static InstancedVertexArray instancedVertexArray = new InstancedVertexArray();

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
    public static void renderInstancedLimited(ArrayList<Matrix4f> matrices, EnumTiles tileType){
        Models.SPRITE_SHEET.bind();
        INSTANCED_SHADER.use();
        INSTANCED_SHADER.setMatrixArray(matrices);
        tileType.texturedModel.getVertexArray().bind();
        tileType.texturedModel.getVertexArray().drawInstanced(matrices.size());
    }

    public static void renderInstanced(Grid.GridCell gridCell){
        Models.SPRITE_SHEET.bind();
        INSTANCED_SHADER_2.use();
        instancedVertexArray.setAllAttributes(gridCell.getVertices(), gridCell.getTextureCoordinates(), gridCell.getMatrices());
        instancedVertexArray.bind();
        instancedVertexArray.drawInstanced(gridCell.getCount());
    }

    /**
     * Deletes the shaders.
     */
    public static void deleteShaders(){
        INSTANCED_SHADER.delete();
        INSTANCED_SHADER_2.delete();
    }
}
