package main.java;

import main.java.graphics.TexturedModel;
import main.java.graphics.VertexArray;
import main.java.math.Vector3f;

class Obstacle extends TexturedModel {

    Obstacle(){
        this(new Vector3f(), 0.0f, 1.0f);
    }

    Obstacle(Vector3f position, float rotation, float scale){
        super(position, rotation, scale);
        float[] vertices = {
                1.0f, 1.0f,
                1.0f, -1.0f,
                -1.0f, 1.0f,
                -1.0f, -1.0f
        };
        int[] indices = {
                0, 1, 2,
                1, 3, 2
        };
        int[] texcoords = {
                0, 0,
                0, 1,
                1, 0,
                1, 1
        };
        this.setVertexArray(new VertexArray(vertices, indices, texcoords));
        this.setShader("src/main/java/shaders/square_shader");
        this.setTexture("src/main/resources/black.png");
        this.setMatrixLocation();
    }
}
