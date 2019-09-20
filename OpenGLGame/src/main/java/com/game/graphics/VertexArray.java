package com.game.graphics;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL46.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL46.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL46.GL_FLOAT;
import static org.lwjgl.opengl.GL46.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL46.GL_TRIANGLES;
import static org.lwjgl.opengl.GL46.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL46.glBindBuffer;
import static org.lwjgl.opengl.GL46.glBindVertexArray;
import static org.lwjgl.opengl.GL46.glBufferData;
import static org.lwjgl.opengl.GL46.glDrawElements;
import static org.lwjgl.opengl.GL46.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL46.glGenBuffers;
import static org.lwjgl.opengl.GL46.glGenVertexArrays;
import static org.lwjgl.opengl.GL46.glVertexAttribPointer;

/**
 * Class for handling buffers containing the vertex data of an object.
 */
class VertexArray {

    private int vao = glGenVertexArrays(), vbo = glGenBuffers(), tbo = glGenBuffers(), ebo = glGenBuffers(), length;

    private static final float[] defVertices = {
            0.0f, 0.0f,     //Top-left
            1.0f, 0.0f,     //Top-right
            1.0f, -1.0f,    //Bottom-right
            0.0f, -1.0f     //Bottom-left
    };

    private static final int[] defIndices = {
            0, 1, 2,
            0, 2, 3
    };

    private static final float[] defTexcoords = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f
    };

    VertexArray()
    {
        this(defVertices, defIndices, defTexcoords);
    }

    VertexArray(float[] textureCoordinates)
    {
        this(defVertices, defIndices, textureCoordinates);
    }

    VertexArray(float[] vertices, float[] textureCoordinates){
        this(vertices, defIndices, textureCoordinates);
    }

    private VertexArray(float[] vertices, int[] indices, float[] textureCoordinates)
    {
        length = indices.length;

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 8, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, tbo);
        glBufferData(GL_ARRAY_BUFFER, textureCoordinates, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 8, 0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindVertexArray(0);
    }

    void draw(){
        glDrawElements(GL_TRIANGLES, length, GL_UNSIGNED_INT, 0);
    }

    void drawInstanced(int count){
        glDrawElementsInstanced(GL_TRIANGLES, length, GL_UNSIGNED_INT, 0, count);
    }

    void bind(){
        glBindVertexArray(vao);
    }

    void unbind(){
        glBindVertexArray(0);
    }

    void delete(){
        unbind();
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(tbo);
        glDeleteBuffers(ebo);
    }
}