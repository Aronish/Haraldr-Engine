package com.game.graphics;

import com.game.ArrayUtils;
import com.game.Camera;
import com.game.gameobject.Entity;
import com.game.gameobject.GameObject;
import com.game.math.Matrix4f;
import com.game.world.Grid;

import java.util.ArrayList;
import java.util.List;

import static com.game.Application.MAIN_LOGGER;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class Renderer
{
    private static final Shader SHADER = new Shader("shaders/shader");
    private static final Shader INSTANCED_SHADER = new Shader("shaders/instanced_shader.vert", "shaders/shader.frag");

    public static void clear()
    {
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public static void setClearColor(float r, float g, float b, float a)
    {
        glClearColor(r, g, b, a);
    }

    /**
     * Binds all the normal stuff like, sprite sheets, shaders and sets the view and projection uniforms.
     * Issues a normal draw call.
     * @param entity the entity to render.
     */
    public static void render(Camera camera, Entity entity)
    {
        SHADER.use();
        SHADER.setMatrix(entity.getMatrixArray(), "matrix");
        SHADER.setMatrix(camera.getViewMatrix().matrix, "view");
        SHADER.setMatrix(Matrix4f.orthographic.matrix, "projection");
        entity.getGameObjectType().getModel().getVertexArray().bind();
        entity.getGameObjectType().getModel().getVertexArray().draw();

        glBindVertexArray(0); //Without this, the last thing rendered before text is rendered will capture some buffer bindings in TextRenderer.
    }

    /**
     * Renders every type of tile in the provided list of GridCells using instancing.
     */
    public static void renderGridCells(Camera camera, List<Grid.GridCell> gridCells){
        Models.SPRITE_SHEET.bind();
        INSTANCED_SHADER.use();
        INSTANCED_SHADER.setMatrix(camera.getViewMatrix().matrix, "view");
        INSTANCED_SHADER.setMatrix(Matrix4f.orthographic.matrix, "projection");
        for (GameObject tileType : GameObject.values()){
            InstancedRenderer.matrices.clear();
            for (Grid.GridCell gridCell : gridCells){
                InstancedRenderer.matrices.addAll(gridCell.getMatrices(tileType));
            }
            if (InstancedRenderer.matrices.size() != 0){
                renderInstanced(tileType);
            }
        }
    }

    /**
     * Binds all the normal stuff like, sprite sheets, shaders and sets the view and projection uniforms.
     * Sends all matrices to be rendered to the buffer and issues an instanced draw call.
     * @param tileType the type of tile currently being rendered.
     */
    private static void renderInstanced(GameObject tileType)
    {
        InstancedRenderer.instancedMatrixBuffer.bind();
        glBufferSubData(GL_ARRAY_BUFFER, 0, ArrayUtils.toPrimitiveArrayF(InstancedRenderer.matrices));
        tileType.getModel().getVertexArray().bind();
        tileType.getModel().getVertexArray().drawInstanced(InstancedRenderer.matrices.size() / 16);
    }

    public static void deleteShaders()
    {
        SHADER.delete();
        INSTANCED_SHADER.delete();
    }

    /**
     * Apparently necessary for some weird "bug" with static initialization.
     */
    private static class InstancedRenderer
    {
        private static ArrayList<Float> matrices = new ArrayList<>();
        private static VertexBuffer instancedMatrixBuffer;

        static
        {
            VertexBufferLayout layout = new VertexBufferLayout
            (
                    new VertexBufferElement(ShaderDataType.MAT4, false),
                    new VertexBufferElement(ShaderDataType.MAT4, false),
                    new VertexBufferElement(ShaderDataType.MAT4, false),
                    new VertexBufferElement(ShaderDataType.MAT4, false)
            );
            instancedMatrixBuffer = new VertexBuffer(10000000, layout);
            setupInstancedBuffer();
        }

        /**
         * Sets up all vertex arrays to know about the instanced matrix attribute.
         * (At current zoom level, a buffer size of ~10 Mb should be enough.)
         */
        private static void setupInstancedBuffer()
        {
            for (GameObject tileType : GameObject.values())
            {
                tileType.getModel().getVertexArray().bind();
                int nextAttribIndex = tileType.getModel().getVertexArray().getNextAttribIndex();
                for (VertexBufferElement element : instancedMatrixBuffer.getLayout())
                {
                    MAIN_LOGGER.info("INDEX" + nextAttribIndex);
                    glEnableVertexAttribArray(nextAttribIndex);
                    glVertexAttribPointer(nextAttribIndex, element.getSize(), element.getType(), element.isNormalized(), instancedMatrixBuffer.getLayout().getStride(), element.getOffset());
                    glVertexAttribDivisor(nextAttribIndex, 1);
                    nextAttribIndex += 1;
                }
                tileType.getModel().getVertexArray().setNextAttribIndex(nextAttribIndex);
            }
        }
    }
}
