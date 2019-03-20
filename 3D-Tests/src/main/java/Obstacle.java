package main.java;

import main.java.graphics.TexturedModel;
import main.java.math.Vector3f;

class Obstacle extends TexturedModel {

    Obstacle(){
        this(new Vector3f(), 0.0f, 1.0f);
    }

    Obstacle(Vector3f position, float rotation, float scale){
        super(position, rotation, scale);
        this.setVertexArray();
        this.setShader("src/main/java/shaders/square_shader");
        this.setTexture("src/main/resources/black.png");
        this.setMatrixLocation();
    }
}
