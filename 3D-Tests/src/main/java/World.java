package main.java;

import main.java.graphics.TexturedModel;
import main.java.graphics.VertexArray;
import main.java.math.Vector3f;

class World extends TexturedModel {

    private float[] vertices = {
            20.0f, 20.0f,
            20.0f, -20.0f,
            -20.0f, 20.0f,
            -20.0f, -20.0f
    };
    private int[] indices = {
            0, 1, 2,
            1, 3, 2
    };
    private int[] texcoords = {
            20, 20,
            20, 0,
            0, 20,
            0, 0
    };

    World(){
        this(new Vector3f(), 0.0f, 1.0f);
    }

    World(Vector3f position, float rotation, float scale){
        super(position, rotation, scale);
        this.setVertexArray(new VertexArray(vertices, indices, texcoords));
        this.setShader("src/main/java/shaders/square_shader");
        this.setTexture("src/main/resources/grass.png");
        this.setMatrixLocation();
    }
}