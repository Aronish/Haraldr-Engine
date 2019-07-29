package com.game.graphics;

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

    private static final Shader SQUARE_SHADER_INSTANCED = new Shader("shaders/square_shader_instanced");
    private static final Shader SQUARE_SHADER_INSTANCED_2 = new Shader("shaders/square_shader_instanced2.vert", "shaders/square_shader_instanced.frag");

    private static InstancedVertexArray instancedVertexArray;

    static {
        instancedVertexArray = new InstancedVertexArray();
    }

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
        SQUARE_SHADER_INSTANCED.use();
        SQUARE_SHADER_INSTANCED.setMatrixArray(matrices);
        tileType.texturedModel.getVertexArray().bind();
        tileType.texturedModel.getVertexArray().drawInstanced(matrices.size());
    }

    public static void renderInstanced(Grid.GridCell gridCell){
        Models.SPRITE_SHEET.bind();
        SQUARE_SHADER_INSTANCED_2.use();
        instancedVertexArray.setAllAttributes(gridCell.getVertices(), gridCell.getTextureCoordinates(), gridCell.getMatrices());
        instancedVertexArray.bind();
        instancedVertexArray.drawInstanced(gridCell.getCount());
    }

    /**
     * Deletes the shaders.
     */
    public static void deleteShaders(){
        SQUARE_SHADER_INSTANCED.delete();
        SQUARE_SHADER_INSTANCED_2.delete();
    }
}
