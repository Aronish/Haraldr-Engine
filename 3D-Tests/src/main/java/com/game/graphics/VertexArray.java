package com.game.graphics;

import com.game.debug.Logger;
import com.game.level.Grid;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
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

    private int vao, vbo, tbo, ebo, length;

    private static final float[] defVertices = {
            1.0f, 0.0f,     //Top-right
            1.0f, -1.0f,    //Bottom-right
            0.0f, 0.0f,     //Top-left
            0.0f, -1.0f     //Bottom-left
    };

    private static final int[] defIndices = {
            2, 0, 1,
            2, 1, 3
    };

    private static final float[] defTexcoords = {
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f
    };

    /**
     * Default constructor if no arguments are provided. Uses the predefined data above.
     */
    VertexArray(){
        this(defVertices, defIndices, defTexcoords);
    }

    VertexArray(float[] textureCoordinates){
        this(defVertices, defIndices, textureCoordinates);
    }

    VertexArray(float[] vertices, float[] textureCoordinates){
        this(vertices, defIndices, textureCoordinates);
    }

    /**
     * Constructor with parameters for vertex positions, indices and texture coordinates.
     * @param vertices an array of floats, the vertices.
     * @param indices an array of integers, the indices which tells OpenGL in what order to draw the vertices.
     * @param textureCoordinates an array of integers, the coordinates of the texture coordinates.
     */
    private VertexArray(float[] vertices, int[] indices, float[] textureCoordinates){
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();
        tbo = glGenBuffers();
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

    /**
     * Invokes an OpenGL draw call to draw using the contents of the bound buffers.
     */
    void draw(){
        glDrawElements(GL_TRIANGLES, length, GL_UNSIGNED_INT, 0);
    }

    void drawInstanced(int count){
        glDrawElementsInstanced(GL_TRIANGLES, length, GL_UNSIGNED_INT, 0, count);
    }

    /**
     * Binds the VAO for setting the state or drawing.
     */
    void bind(){
        glBindVertexArray(vao);
    }

    /**
     * Unbinds the VAO to avoid weird conflicts.
     */
    private void unbind(){
        glBindVertexArray(0);
    }

    /**
     * Deletes the vertex array and associated buffers.
     */
    void delete(){
        unbind();
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(tbo);
        glDeleteBuffers(ebo);
    }
}