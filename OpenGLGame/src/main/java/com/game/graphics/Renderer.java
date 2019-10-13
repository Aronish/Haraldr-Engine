package com.game.graphics;

import com.game.ArrayUtils;
import com.game.Camera;
import com.game.gameobject.Entity;
import com.game.gameobject.GameObject;
import com.game.math.Matrix4f;
import com.game.world.Grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.game.Application.MAIN_LOGGER;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;

public class Renderer
{
    private static MultiDrawIndirectRenderer multiDrawIndirectRenderer = new MultiDrawIndirectRenderer();
    private static HashMap<GameObject, Integer> tileCounts = new HashMap<>(), matrixOffsets = new HashMap<>();
    private static List<Integer> indirectBuffer = new ArrayList<>();

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
     * Issues a normal draw call. GUIComponents have their own render method.
     * @param entity the entity to render.
     */
    public static void render(Camera camera, Shader shader, Entity entity)
    {
        shader.use();
        shader.setMatrix(entity.getMatrixArray(), "matrix");
        shader.setMatrix(camera.getViewMatrix().matrix, "view");
        shader.setMatrix(Matrix4f.orthographic.matrix, "projection");
        entity.getGameObjectType().getModel().getVertexArray().bind();
        entity.getGameObjectType().getModel().getVertexArray().draw();

        glBindVertexArray(0); //Without this, the last thing rendered before text is rendered will capture some buffer bindings in TextRenderer.
    }

    /**
     * Renders every type of tile in the provided list of GridCells using instancing.
     */
    public static void renderGridCells(Camera camera, Shader shader, List<Grid.GridCell> gridCells)
    {
        Models.SPRITE_SHEET.bind();
        shader.use();
        shader.setMatrix(camera.getViewMatrix().matrix, "view");
        shader.setMatrix(Matrix4f.orthographic.matrix, "projection");
        for (GameObject tileType : GameObject.values())
        {
            InstancedRenderer.matrices.clear();
            for (Grid.GridCell gridCell : gridCells)
            {
                InstancedRenderer.matrices.addAll(gridCell.getMatrices(tileType));
            }
            if (InstancedRenderer.matrices.size() != 0)
            {
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

    public static void multiRenderIndirectGridCells(Camera camera, Shader shader, List<Grid.GridCell> gridCells)
    {
        Models.SPRITE_SHEET.bind();
        shader.use();
        shader.setMatrix(camera.getViewMatrix().matrix, "view");
        shader.setMatrix(Matrix4f.orthographic.matrix, "projection");
        /////COLLECT MATRICES//////////////////////////////////////////////
        multiDrawIndirectRenderer.matrices.clear();
        tileCounts.replaceAll((key, value) -> 0);
        matrixOffsets.replaceAll((key, value) -> 0);
        indirectBuffer.clear();
        int matrixStride = 0;
        int objectCount = 0;
        for (GameObject gameObject : GameObject.instancedObjects)
        {
            for (Grid.GridCell gridCell : gridCells)
            {
                multiDrawIndirectRenderer.matrices.addAll(gridCell.getMatrices(gameObject));
                int prevCount = tileCounts.getOrDefault(gameObject, 0);
                tileCounts.put(gameObject, prevCount + gridCell.getTileCount(gameObject));
            }
            matrixOffsets.put(gameObject, matrixStride);
            matrixStride += tileCounts.get(gameObject);

            List<Integer> entry = new ArrayList<>();
            entry.add(6);
            entry.add(tileCounts.get(gameObject));
            entry.add(6);
            entry.add(8 * objectCount);
            entry.add(matrixOffsets.get(gameObject));
            indirectBuffer.addAll(entry);
            ++objectCount;
        }

        multiDrawIndirectRenderer.instancedMatrixBuffer.bind();
        glBufferSubData(GL_ARRAY_BUFFER, 0, ArrayUtils.toPrimitiveArrayF(multiDrawIndirectRenderer.matrices));

        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, MultiDrawIndirectRenderer.indirectBuffer);
        glBufferSubData(GL_DRAW_INDIRECT_BUFFER, 0, ArrayUtils.toPrimitiveArrayI(indirectBuffer));

        multiRenderIndirect(indirectBuffer.size() / 5);
    }

    private static void multiRenderIndirect(int count)
    {
        multiDrawIndirectRenderer.vao.bind();
        multiDrawIndirectRenderer.vao.multiDrawIndirect(count);
        multiDrawIndirectRenderer.vao.unbind();
    }

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
            instancedMatrixBuffer.bind();
            /////SETUP ATTRIBUTES//////////////////////////
            for (GameObject tileType : GameObject.values())
            {
                if (tileType.instanced)
                {
                    tileType.getModel().getVertexArray().bind();
                    int nextAttribIndex = tileType.getModel().getVertexArray().getNextAttribIndex();
                    for (VertexBufferElement element : instancedMatrixBuffer.getLayout())
                    {
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
}
