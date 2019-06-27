package main.java.graphics;

import main.java.debug.Logger;
import main.java.level.tiles.EnumTiles;
import main.java.math.Matrix4f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class InstancedRenderer {

    private static final Shader SQUARE_SHADER_INSTANCED = new Shader("src/main/java/shaders/square_shader_instanced");

    public static void clear(){
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public static void renderInstanced(ArrayList<Matrix4f> matrices, EnumTiles tileType){
        SQUARE_SHADER_INSTANCED.use();
        SQUARE_SHADER_INSTANCED.setMatrixArray(matrices);
        switch (tileType){
            case GRASS:
                Models.GRASS_TILE.getVertexArray().bind();
                Models.GRASS_TILE.getTexture().bind();
                Models.GRASS_TILE.getVertexArray().drawInstanced(matrices.size());
                break;
            case DIRT:
                Models.DIRT_TILE.getVertexArray().bind();
                Models.DIRT_TILE.getTexture().bind();
                Models.DIRT_TILE.getVertexArray().drawInstanced(matrices.size());
                break;
            case GRASS_SNOW:
                Models.SNOW_TILE.getVertexArray().bind();
                Models.SNOW_TILE.getTexture().bind();
                Models.SNOW_TILE.getVertexArray().drawInstanced(matrices.size());
                break;
            case STONE:
                Models.STONE_TILE.getVertexArray().bind();
                Models.STONE_TILE.getTexture().bind();
                Models.STONE_TILE.getVertexArray().drawInstanced(matrices.size());
                break;
        }
    }

    public static void deleteShaders(){
        SQUARE_SHADER_INSTANCED.delete();
    }
}
