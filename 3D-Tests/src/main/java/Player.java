package main.java;

import main.java.graphics.TexturedModel;
import main.java.graphics.VertexArray;
import main.java.math.Matrix4f;
import main.java.math.Vector3f;

class Player extends TexturedModel {

    Player(){
        float[] vertices = {
                1f, 1f, -2.0f,
                1f, -1f, -2.0f,
                -1f, 1f, -2.0f,
                -1f, -1f, -2.0f
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
        this.setShader("src/main/java/shaders/player_shader");
        this.setTexture("src/main/resources/player.png");
        this.updateMatrix(new Vector3f(), 0.0f, 1.0f);
        this.setMatrixLocation(true);
    }

    @Override
    public void updateMatrix(Vector3f position, float rotation, float scale) {
        this.matrix = new Matrix4f().MP(position, rotation, scale);
    }
}