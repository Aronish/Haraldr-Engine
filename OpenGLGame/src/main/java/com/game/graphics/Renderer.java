package com.game.graphics;

import com.game.ArrayUtils;
import com.game.Camera;
import com.game.Main;
import com.game.gameobject.Entity;
import com.game.gameobject.GameObject;
import com.game.math.Matrix4f;
import com.game.world.Grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.game.Application.MAIN_LOGGER;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL45.glCreateBuffers;

public class Renderer
{
    private static final RenderSystem renderSystem;

    static
    {
        if (Main.MULTI_RENDER)
        {
            renderSystem = new MultiDrawIndirectRenderer();
            MAIN_LOGGER.info("Render System: MultiDrawIndirect");
        }else{
            renderSystem = new InstancedRenderer();
            MAIN_LOGGER.info("Render System: Instancing");
        }
    }

    public static void clear()
    {
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public static void setClearColor(float r, float g, float b, float a)
    {
        glClearColor(r, g, b, a);
    }

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

    public static void renderGridCells(Camera camera, List<Grid.GridCell> gridCells)
    {
        Models.SPRITE_SHEET.bind();
        renderSystem.renderGridCells(camera, gridCells);
    }

/////////////////////////////////////////////////////
/////INSTANCED RENDERER//////////////////////////////
/////////////////////////////////////////////////////

    private static class InstancedRenderer implements RenderSystem
    {
        private ArrayList<Float> matrices = new ArrayList<>();
        private VertexBuffer instancedMatrixBuffer;

        private InstancedRenderer()
        {
            VertexBufferLayout layout = new VertexBufferLayout
            (
                    new VertexBufferElement(ShaderDataType.MAT4),
                    new VertexBufferElement(ShaderDataType.MAT4),
                    new VertexBufferElement(ShaderDataType.MAT4),
                    new VertexBufferElement(ShaderDataType.MAT4)
            );
            instancedMatrixBuffer = new VertexBuffer(2500000, layout);
            /////SETUP ATTRIBUTES//////////////////////////
            for (GameObject gameObject : GameObject.instancedObjects)
            {
                gameObject.getModel().getVertexArray().bind();
                instancedMatrixBuffer.bind();//Must be bound after model due to the vertex buffer being bound with the array.
                int nextAttribIndex = gameObject.getModel().getVertexArray().getNextAttribIndex();
                for (VertexBufferElement element : instancedMatrixBuffer.getLayout())
                {
                    glEnableVertexAttribArray(nextAttribIndex);
                    glVertexAttribPointer(nextAttribIndex, element.getSize(), element.getType(), element.isNormalized(), instancedMatrixBuffer.getLayout().getStride(), element.getOffset());
                    glVertexAttribDivisor(nextAttribIndex, 1);
                    nextAttribIndex += 1;
                }
                gameObject.getModel().getVertexArray().setNextAttribIndex(nextAttribIndex);
            }
        }

        @Override
        public void renderGridCells(Camera camera, List<Grid.GridCell> gridCells)
        {
            Shader.INSTANCED_SHADER.use();
            Shader.INSTANCED_SHADER.setMatrix(camera.getViewMatrix().matrix, "view");
            Shader.INSTANCED_SHADER.setMatrix(Matrix4f.orthographic.matrix, "projection");
            instancedMatrixBuffer.bind();
            for (GameObject gameObject : GameObject.instancedObjects)
            {
                matrices.clear();
                for (Grid.GridCell gridCell : gridCells)
                {
                    matrices.addAll(gridCell.getMatrices(gameObject));
                }
                if (matrices.size() != 0)
                {
                    glBufferSubData(GL_ARRAY_BUFFER, 0, ArrayUtils.toPrimitiveArrayF(matrices));
                    gameObject.getModel().getVertexArray().bind();
                    gameObject.getModel().getVertexArray().drawInstanced(matrices.size() / 16);
                }
            }
        }
    }

/////////////////////////////////////////////////////
/////MULTIDRAWINDIRECTRENDERER///////////////////////
/////////////////////////////////////////////////////

    private static class MultiDrawIndirectRenderer implements RenderSystem
    {
        private VertexArray vao;
        private int indirectBuffer = glCreateBuffers();

        private List<Float> matrices = new ArrayList<>();
        private VertexBuffer instancedMatrixBuffer;

        private HashMap<GameObject, Integer> instanceCounts = new HashMap<>(), matrixOffsets = new HashMap<>();
        private List<Integer> indirectData = new ArrayList<>();

        private MultiDrawIndirectRenderer()
        {
            List<Integer> indirectIndices = new ArrayList<>();
            for (int i = 0; i < 6; ++i)
            {
                indirectIndices.add(4 * i);
                indirectIndices.add(4 * i + 1);
                indirectIndices.add(4 * i + 2);
                indirectIndices.add(4 * i);
                indirectIndices.add(4 * i + 2);
                indirectIndices.add(4 * i + 3);
            }
            vao = new VertexArray(ArrayUtils.toPrimitiveArrayI(indirectIndices));
            /////MODEL DATA STORE///////////////////////////////////
            List<Float> modelVertexData = new ArrayList<>();
            for (GameObject gameObject : GameObject.instancedObjects)
            {
                float[] vertexData = gameObject.getModel().getVertexArray().getVertexBuffer().getData();
                modelVertexData.addAll(ArrayUtils.toList(vertexData));
            }

            VertexBufferLayout modelLayout = new VertexBufferLayout
                    (
                            new VertexBufferElement(ShaderDataType.FLOAT2),
                            new VertexBufferElement(ShaderDataType.FLOAT2)
                    );
            VertexBuffer modelVertexBuffer = new VertexBuffer(ArrayUtils.toPrimitiveArrayF(modelVertexData), modelLayout);
            vao.setVertexBuffer(modelVertexBuffer);

            /////MATRICES/////////////////////////////////////
            VertexBufferLayout layout = new VertexBufferLayout
                    (
                            new VertexBufferElement(ShaderDataType.MAT4),
                            new VertexBufferElement(ShaderDataType.MAT4),
                            new VertexBufferElement(ShaderDataType.MAT4),
                            new VertexBufferElement(ShaderDataType.MAT4)
                    );
            instancedMatrixBuffer = new VertexBuffer(2500000, layout);

            vao.bind();
            instancedMatrixBuffer.bind();
            int nextAttribIndex = vao.getNextAttribIndex();
            for (VertexBufferElement element : instancedMatrixBuffer.getLayout())
            {
                glEnableVertexAttribArray(nextAttribIndex);
                glVertexAttribPointer(nextAttribIndex, element.getSize(), element.getType(), element.isNormalized(), instancedMatrixBuffer.getLayout().getStride(), element.getOffset());
                glVertexAttribDivisor(nextAttribIndex, 1);
                nextAttribIndex += 1;
            }
            vao.setNextAttribIndex(nextAttribIndex);
            vao.unbind();

            glBindBuffer(GL_DRAW_INDIRECT_BUFFER, indirectBuffer);
            glBufferData(GL_DRAW_INDIRECT_BUFFER, 1000, GL_DYNAMIC_DRAW); //TODO: Figure out size.
            glBindBuffer(GL_DRAW_INDIRECT_BUFFER, 0);
        }

        @Override
        public void renderGridCells(Camera camera, List<Grid.GridCell> gridCells)
        {
            Shader.MULTI_DRAW_SHADER.use();
            Shader.MULTI_DRAW_SHADER.setMatrix(camera.getViewMatrix().matrix, "view");
            Shader.MULTI_DRAW_SHADER.setMatrix(Matrix4f.orthographic.matrix, "projection");
            /////COLLECT MATRICES//////////////////////////////////////////////
            matrices.clear();
            instanceCounts.replaceAll((key, value) -> 0);
            matrixOffsets.replaceAll((key, value) -> 0);
            indirectData.clear();
            int matrixStride = 0;
            int objectCount = 0;
            for (GameObject gameObject : GameObject.instancedObjects)
            {
                for (Grid.GridCell gridCell : gridCells)
                {
                    matrices.addAll(gridCell.getMatrices(gameObject));
                    int prevCount = instanceCounts.getOrDefault(gameObject, 0);
                    instanceCounts.put(gameObject, prevCount + gridCell.getTileCount(gameObject));
                }
                matrixOffsets.put(gameObject, matrixStride);
                matrixStride += instanceCounts.getOrDefault(gameObject, 0);

                List<Integer> entry = new ArrayList<>();
                entry.add(6);                                                      //Indices per instance.
                entry.add(instanceCounts.getOrDefault(gameObject, 0)); //Amount of instances of current object type.
                entry.add(0);                                                      //First index offset, should be 0.
                entry.add(4 * objectCount);                                        //Offset into model data.
                entry.add(matrixOffsets.get(gameObject));                          //Offset into instanced matrix attribute data.
                indirectData.addAll(entry);
                ++objectCount;
            }

            instancedMatrixBuffer.bind();
            glBufferSubData(GL_ARRAY_BUFFER, 0, ArrayUtils.toPrimitiveArrayF(matrices));

            glBindBuffer(GL_DRAW_INDIRECT_BUFFER, indirectBuffer);
            glBufferSubData(GL_DRAW_INDIRECT_BUFFER, 0, ArrayUtils.toPrimitiveArrayI(indirectData));

            vao.bind();
            vao.multiDrawIndirect(indirectData.size() / 5);
        }
    }
}