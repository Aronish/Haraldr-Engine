package com.game.graphics;

import com.game.level.Grid;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

class InstancedVertexArray {

    private static final int[] defIndices = {
            2, 0, 1,
            2, 1, 3
    };

    private int vao, vbo, tbo, ebo, mbo, length;

    InstancedVertexArray(){
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();
        tbo = glGenBuffers();
        mbo = glGenBuffers();
        length = defIndices.length;
        int tilesPerGridCell = (int) Math.pow(Grid.GRID_SIZE, 2);

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, 32 * tilesPerGridCell, GL_DYNAMIC_DRAW);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 8, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribDivisor(0, 1);

        glBindBuffer(GL_ARRAY_BUFFER, tbo);
        glBufferData(GL_ARRAY_BUFFER, 32 * tilesPerGridCell, GL_DYNAMIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 8, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribDivisor(1, 1);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, defIndices, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, mbo);
        glBufferData(GL_ARRAY_BUFFER, 4 * Grid.CONT_MAT4_ARRAY_LENGTH, GL_DYNAMIC_DRAW);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 4, GL_FLOAT, false, 64, 0);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 4, GL_FLOAT, false, 64, 16);
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 4, GL_FLOAT, false, 64, 32);
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(5, 4, GL_FLOAT, false, 64, 48);

        glVertexAttribDivisor(2, 1);
        glVertexAttribDivisor(3, 1);
        glVertexAttribDivisor(4, 1);
        glVertexAttribDivisor(5, 1);

        glBindVertexArray(0);
    }

    void setAllAttributes(float[] vertices, float[] textureCoordinates, float[] matrices){
        setVertices(vertices);
        setTextureCoordinates(textureCoordinates);
        setMatrices(matrices);
    }

    private void setVertices(float[] vertices){
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
    }

    private void setTextureCoordinates(float[] textureCoordinates){
        glBindBuffer(GL_ARRAY_BUFFER, tbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, textureCoordinates);
    }

    private void setMatrices(float[] matrices){
        glBindBuffer(GL_ARRAY_BUFFER, mbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, matrices);
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
