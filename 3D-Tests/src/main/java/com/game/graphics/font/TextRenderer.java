package com.game.graphics.font;

import com.game.graphics.Shader;
import com.game.gui.GUIComponent;
import com.game.gui.GUILabel;
import com.game.math.Matrix4f;
import com.game.math.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
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

public class TextRenderer {

    private static final Shader FONT_SHADER = new Shader("shaders/font");

    private int textVAO = glCreateVertexArrays(), textVBO = glGenBuffers(), textEBO = glGenBuffers();

    public TextRenderer()
    {
        glBindVertexArray(textVAO);
        glBindBuffer(GL_ARRAY_BUFFER, textVBO);
        glBufferData(GL_ARRAY_BUFFER, 10000, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 28, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 28, 8);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 28, 16);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, textEBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, 10000, GL_STATIC_DRAW);
        glBindVertexArray(0);
    }

    public void setupRenderData(Font.TextRenderData textRenderData)
    {
        glBindVertexArray(textVAO);
        glBindBuffer(GL_ARRAY_BUFFER, textVBO);
        glBufferSubData(GL_ARRAY_BUFFER, 0, toPrimitiveArrayF(textRenderData.vertexData));
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, textEBO);
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, toPrimitiveArrayI(textRenderData.indices));
        glBindVertexArray(0);
    }

    private float[] toPrimitiveArrayF(List<Float> list)
    {
        float[] primitiveArray = new float[list.size()];
        int insertIndex = 0;
        for (Float element : list)
        {
            primitiveArray[insertIndex++] = element;
        }
        return primitiveArray;
    }

    private int[] toPrimitiveArrayI(List<Integer> list)
    {
        int[] primitiveArray = new int[list.size()];
        int insertIndex = 0;
        for (Integer element : list)
        {
            primitiveArray[insertIndex++] = element;
        }
        return primitiveArray;
    }

    public void render(GUILabel guiLabel)
    {
        FONT_SHADER.use();
        FONT_SHADER.setMatrix(guiLabel.getMatrix().matrix, "model");
        FONT_SHADER.setMatrix(Matrix4f._orthographic.matrix, "projection");
        glBindVertexArray(textVAO);
        guiLabel.bind();
        glBindBuffer(GL_ARRAY_BUFFER, textVBO);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, textEBO);
        glDrawElements(GL_TRIANGLES, guiLabel.getTextRenderData().indices.size(), GL_UNSIGNED_INT, 0);
    }
}
