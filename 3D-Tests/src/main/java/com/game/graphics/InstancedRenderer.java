package com.game.graphics;

import com.game.Camera;
import com.game.world.Grid;
import com.game.gameobject.GameObject;
import com.game.math.Matrix4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 * Renderer that uses instancing rather than a new draw call for every Entity.
 */
public class InstancedRenderer {

    private static final Shader INSTANCED_SHADER = new Shader("shaders/instanced_shader.vert", "shaders/shader.frag");

    private static ArrayList<Float> matrices = new ArrayList<>();
    private static int instancedMBO = glGenBuffers();

    static { setupInstancedBuffer(); }

    private static float[] matricesAsPrimitiveArray(){
        float[] primitiveArray = new float[matrices.size()];
        int i = 0;
        for (Float element : matrices){
            primitiveArray[i++] = element;
        }
        return primitiveArray;
    }

    /**
     * Sets up all vertex arrays to know about the instanced matrix attribute.
     * (At current zoom level, a buffer size of ~10 Mb should be enough.)
     */
    private static void setupInstancedBuffer(){
        glBindBuffer(GL_ARRAY_BUFFER, instancedMBO);
        glBufferData(GL_ARRAY_BUFFER, 10000000, GL_DYNAMIC_DRAW);

        for (GameObject tileType : GameObject.values()){
            tileType.model.getVertexArray().bind();
            glEnableVertexAttribArray(2);
            glVertexAttribPointer(2, 4, GL_FLOAT, false, 64, 0);
            glEnableVertexAttribArray(3);
            glVertexAttribPointer(3, 4, GL_FLOAT, false, 64, 16);
            glEnableVertexAttribArray(4);
            glVertexAttribPointer(4, 4, GL_FLOAT, false, 64, 32);
            glEnableVertexAttribArray(5);
            glVertexAttribPointer(5, 4, GL_FLOAT, false, 64, 48);

            glVertexAttribDivisor(2, 1);
            glVertexAttribDivisor(3, 1);
            glVertexAttribDivisor(4, 1);
            glVertexAttribDivisor(5, 1);

            glBindVertexArray(0);
        }
    }

    /**
     * Renders every type of tile in the provided list of GridCells using instancing.
     */
    public static void renderGridCells(Camera camera, List<Grid.GridCell> gridCells){
        for (GameObject tileType : GameObject.values()){
            matrices.clear();
            for (Grid.GridCell gridCell : gridCells){
                matrices.addAll(gridCell.getMatrices(tileType));
            }
            if (matrices.size() != 0){
                renderInstanced(camera, tileType);
            }
        }
    }

    /**
     * Binds all the normal stuff like, sprite sheets, shaders and sets the view and projection uniforms.
     * Sends all matrices to be rendered to the buffer and issues an instanced draw call.
     * @param tileType the type of tile currently being rendered.
     */
    private static void renderInstanced(Camera camera, GameObject tileType){
        Models.SPRITE_SHEET.bind();
        INSTANCED_SHADER.use();
        INSTANCED_SHADER.setMatrix(camera.getViewMatrix().matrix, "view");
        INSTANCED_SHADER.setMatrix(Matrix4f._orthographic.matrix, "projection");
        glBindBuffer(GL_ARRAY_BUFFER, instancedMBO);
        glBufferSubData(GL_ARRAY_BUFFER, 0, matricesAsPrimitiveArray());
        tileType.model.getVertexArray().bind();
        tileType.model.getVertexArray().drawInstanced(matrices.size() / 16);
    }

    public static void deleteShaders(){
        INSTANCED_SHADER.delete();
    }
}
