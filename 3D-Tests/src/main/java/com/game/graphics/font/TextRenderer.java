package com.game.graphics.font;

import com.game.ArrayUtils;
import com.game.graphics.Shader;
import com.game.gui.GUILabel;
import com.game.math.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;

/**
 * Batches together all vertex data used for rendering text.
 */
public class TextRenderer
{
    private static final Shader FONT_SHADER = new Shader("shaders/font");

    private int textVAO = glCreateVertexArrays(), textVBO = glGenBuffers(), textEBO = glGenBuffers(), textMatrixBuffer = glGenBuffers();
    private List<Float> vertexData = new ArrayList<>(), matrices = new ArrayList<>();
    private List<Integer> indices = new ArrayList<>();

    public TextRenderer()
    {
        initializeBuffers();
    }

    private void initializeBuffers()
    {
        glBindVertexArray(textVAO);
        glBindBuffer(GL_ARRAY_BUFFER, textVBO);
        glBufferData(GL_ARRAY_BUFFER, 0, GL_DYNAMIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 28, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 28, 8);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 28, 16);

        glBindBuffer(GL_ARRAY_BUFFER, textMatrixBuffer);
        glBufferData(GL_ARRAY_BUFFER, 0, GL_DYNAMIC_DRAW);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 4, GL_FLOAT, false, 64, 0);
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 4, GL_FLOAT, false, 64, 16);
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(5, 4, GL_FLOAT, false, 64, 32);
        glEnableVertexAttribArray(6);
        glVertexAttribPointer(6, 4, GL_FLOAT, false, 64, 48);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, textEBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, 0, GL_DYNAMIC_DRAW);
        glBindVertexArray(0);
    }

    private void setupRenderData(List<GUILabel> guiLabels)
    {
        vertexData.clear();
        indices.clear();
        matrices.clear();
        for (GUILabel guiLabel : guiLabels)
        {
            Font.TextRenderData textRenderData = guiLabel.getTextRenderData();
            vertexData.addAll(textRenderData.vertexData);
            List<Float> matrixArray = ArrayUtils.toList(guiLabel.getMatrixArray());
            for (int i = 0; i < textRenderData.vertexData.size(); i += 7)
            {//Each vertex has to have it's own copy of the matrix when not instancing, which is impossible here.
                matrices.addAll(matrixArray);
            }
        }
        indices.addAll(createIndices(vertexData.size() / 7));
        glBindBuffer(GL_ARRAY_BUFFER, textVBO);
        glBufferData(GL_ARRAY_BUFFER, ArrayUtils.toPrimitiveArrayF(vertexData), GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, textMatrixBuffer);
        glBufferData(GL_ARRAY_BUFFER, ArrayUtils.toPrimitiveArrayF(matrices), GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, textEBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ArrayUtils.toPrimitiveArrayI(indices), GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private List<Integer> createIndices(int quadCount)
    {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < quadCount; ++i) {
            indices.add(4 * i);
            indices.add(4 * i + 1);
            indices.add(4 * i + 2);
            indices.add(4 * i);
            indices.add(4 * i + 2);
            indices.add(4 * i + 3);
        }
        return indices;
    }

    public void renderGuiComponents(Map<Font, List<GUILabel>> guiComponents)
    {
        FONT_SHADER.use();
        FONT_SHADER.setMatrix(Matrix4f._orthographic.matrix, "projection");
        for (Font font : guiComponents.keySet())
        {
            List<GUILabel> guiLabels = guiComponents.get(font);
            setupRenderData(guiLabels);
            render(font);
        }
    }

    private void render(Font font)
    {
        font.bind();
        glBindVertexArray(textVAO);
        glDrawElements(GL_TRIANGLES, indices.size(), GL_UNSIGNED_INT, 0);
    }
}
