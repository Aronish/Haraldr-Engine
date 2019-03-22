package main.java.graphics;

import static org.lwjgl.opengl.GL46.*;

/**
 * Class for handling buffers containing the vertex data of an object.
 */
public class VertexArray {

    private int vao, vbo, ebo, tbo, length;
    private float width, height;

    private static float[] defVertices = {
            1.0f, 1.0f,     //Top-right
            1.0f, 0.0f,     //Bottom-right
            0.0f, 1.0f,     //Top-left
            0.0f, 0.0f      //Bottom-left
    };

    private static int[] defIndices = {
            0, 1, 2,
            1, 3, 2
    };

    private static int[] defTexcoords = {
            0, 0,
            0, 1,
            1, 0,
            1, 1
    };

    /**
     * Default constructor if no arguments are provided. Uses the predefined data above.
     */
    public VertexArray(){
        this(defVertices, defIndices, defTexcoords);
    }

    /**
     * Constructor with parameters for vertex positions, indices and texture coordinates.
     * @param vertices an array of floats, the vertices.
     * @param indices an array of integers, the indices which tells OpenGL in what order to draw the vertices.
     * @param texcoords an array of integers, the coordinates of the texture coordinates.
     */
    public VertexArray(float[] vertices, int[] indices, int[] texcoords){
        this.vao = glGenVertexArrays();
        this.vbo = glGenBuffers();
        this.ebo = glGenBuffers();
        this.tbo = glGenBuffers();
        this.length = indices.length;

        this.width = vertices[0] - vertices[4];
        this.height = vertices[1] - vertices[3];

        glBindVertexArray(this.vao);

        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 8, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, this.tbo);
        glBufferData(GL_ARRAY_BUFFER, texcoords, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_INT, false, 8, 0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindVertexArray(0);
    }

    /**
     * Gets the width of the object. Calculated from the vertex coordinates.
     * @return the width of the object.
     */
    public float getWidth(){
        return this.width;
    }

    /**
     * Gets the height of the object. Calculated from the vertex coordinates.
     * @return the height of the object.
     */
    public float getHeight(){
        return this.height;
    }

    /**
     * Invokes an OpenGL draw call to draw using the contents of the bound buffers.
     */
    void draw(){
        glDrawElements(GL_TRIANGLES, this.length, GL_UNSIGNED_INT, 0);
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
    void unbind(){
        glBindVertexArray(0);
    }
}