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

    /**
     * Creates a VertexArray with default vertices and indices and custom texture coordinates.
     * @param textureCoordinates the texture coordinates.
     */
    VertexArray(float[] textureCoordinates){
        this(defVertices, defIndices, textureCoordinates);
    }

    /**
     * Creates a VertexArray with default indices and custom vertices and texture coordinates.
     * @param vertices the vertices.
     * @param textureCoordinates the texture coordinates.
     */
    VertexArray(float[] vertices, float[] textureCoordinates){
        this(vertices, defIndices, textureCoordinates);
    }

    /**
     * Creates a VertexArray with custom vertices, indices and texture coordinates.
     * @param vertices the vertices.
     * @param indices the indices.
     * @param textureCoordinates texture coordinates.
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
     * Invokes a normal OpenGL draw call.
     */
    void draw(){
        glDrawElements(GL_TRIANGLES, length, GL_UNSIGNED_INT, 0);
    }

    /**
     * Invokes an instanced draw call.
     * @param count the number of instances to draw.
     */
    void drawInstanced(int count){
        glDrawElementsInstanced(GL_TRIANGLES, length, GL_UNSIGNED_INT, 0, count);
    }

    /**
     * Binds the VAO.
     */
    void bind(){
        glBindVertexArray(vao);
    }

    /**
     * Unbinds the VAO.
     */
    void unbind(){
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