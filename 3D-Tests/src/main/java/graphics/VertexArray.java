package main.java.graphics;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
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
    private int renderMode;

    private static float[] defVertices = {
            1.0f, 0.0f,     //Top-right
            1.0f, -1.0f,     //Bottom-right
            0.0f, 0.0f,     //Top-left
            0.0f, -1.0f      //Bottom-left
    };

    private static int[] defIndices = {
            0, 1, 2,
            1, 3, 2
    };

    private static float[] defTexcoords = {
            1.0f, -1.0f,
            1.0f, 0.0f,
            0.0f, -1.0f,
            0.0f, 0.0f
    };

    /**
     * Default constructor if no arguments are provided. Uses the predefined data above.
     */
    VertexArray(){
        this(defVertices, defIndices, defTexcoords);
    }

    /**
     * Constructor with parameters for vertex positions, indices and texture coordinates.
     * @param vertices an array of floats, the vertices.
     * @param indices an array of integers, the indices which tells OpenGL in what order to draw the vertices.
     * @param texcoords an array of integers, the coordinates of the texture coordinates.
     */
    VertexArray(float[] vertices, int[] indices, float[] texcoords){
        this.vao = glGenVertexArrays();
        this.vbo = glGenBuffers();
        this.ebo = glGenBuffers();
        this.tbo = glGenBuffers();
        this.length = indices.length;
        this.renderMode = GL_TRIANGLES;

        glBindVertexArray(this.vao);

        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 8, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, this.tbo);
        glBufferData(GL_ARRAY_BUFFER, texcoords, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 8, 0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindVertexArray(0);
    }

    /**
     * Invokes an OpenGL draw call to draw using the contents of the bound buffers.
     */
    void draw(){
        glDrawElements(this.renderMode, this.length, GL_UNSIGNED_INT, 0);
    }

    /**
     * Binds the VAO for setting the state or drawing.
     */
    void bind(){
        glBindVertexArray(this.vao);
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
        glDeleteVertexArrays(this.vao);
        glDeleteBuffers(this.vbo);
        glDeleteBuffers(this.tbo);
        glDeleteBuffers(this.ebo);
    }
}