package main.java.graphics;

import static org.lwjgl.opengl.GL46.*;

public class VertexArray {

    private int vao, vbo, ebo, tbo, length;

    public VertexArray(float[] vertices, int[] indices, int[] texcoords){
        this.vao = glGenVertexArrays();
        this.vbo = glGenBuffers();
        this.ebo = glGenBuffers();
        this.tbo = glGenBuffers();
        this.length = indices.length;

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
    void draw(){
        glDrawElements(GL_TRIANGLES, this.length, GL_UNSIGNED_INT, 0);
    }

    void bind(){
        glBindVertexArray(this.vao);
    }

    void unbind(){
        glBindVertexArray(0);
    }
}