package com.game.graphics;

import com.game.Camera;
import com.game.math.Matrix4f;
import com.game.math.Vector2f;
import com.game.math.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBEasyFont;

import java.nio.ByteBuffer;

import static com.game.Application.MAIN_LOGGER;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class FontRenderer {

    private static final int MAX_LENGTH = 200;
    private static final Shader FONT_SHADER = new Shader("shaders/font");
    private static final Matrix4f matrix = Matrix4f.transform(new Vector3f(0.0f, 240.0f), 0.0f, new Vector2f(10.0f), false);

    private static ByteBuffer vertexData = BufferUtils.createByteBuffer(38 * 270);
    private static int vao = glGenVertexArrays(), vbo = glGenBuffers(), ebo = glGenBuffers(), numQuads;

    private static final int[] defIndices = {
            0, 1, 2,
            0, 2, 3
    };

    static {
        numQuads = STBEasyFont.stb_easy_font_print(0, 0, "HELLO WORLD", null, vertexData);
        MAIN_LOGGER.info(numQuads);

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 1, GL_FLOAT, false, 16, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 1, GL_FLOAT, false, 16, 8);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, defIndices, GL_STATIC_DRAW);

        glBindVertexArray(0);
    }

    public static void renderText(Camera camera){
        FONT_SHADER.use();
        FONT_SHADER.setMatrix(matrix.matrix, "model");
        FONT_SHADER.setMatrix(camera.getViewMatrix().matrix, "view");
        FONT_SHADER.setMatrix(Matrix4f._orthographic.matrix, "projection");
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, numQuads * 6, GL_UNSIGNED_INT, 0);
    }
}
