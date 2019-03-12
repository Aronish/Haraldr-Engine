package main.java;

import main.java.graphics.TexturedModel;
import main.java.graphics.VertexArray;
import main.java.math.Vector3f;

class World extends TexturedModel {

    World(){
        float[] vertices = {
                20.0f, 20.0f, 0.0f,
                20.0f, -20.0f, 0.0f,
                -20.0f, 20.0f, 0.0f,
                -20.0f, -20.0f, 0.0f
        };
        int[] indices = {
                0, 1, 2,
                1, 3, 2
        };
        int[] texcoords = {
                20, 20,
                20, 0,
                0, 20,
                0, 0
        };
        this.setVertexArray(new VertexArray(vertices, indices, texcoords));
        this.setShader("src/main/java/shaders/square_shader");
        this.setTexture("src/main/resources/grass.png");
        this.updateMatrix(new Vector3f(), 0.0f, 1.0f);
        this.setMatrixLocation(false);
    }
}