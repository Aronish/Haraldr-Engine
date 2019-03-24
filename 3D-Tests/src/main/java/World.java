package main.java;

import main.java.graphics.TexturedModel;
import main.java.math.Vector3f;

/**
 * Main world object, on which everything should be located to be inside the world.
 */
class World extends TexturedModel {

    /**
     * Constructor with one parameter for the size
     * @param size the size of the world.
     */
    World(float size){
        this(new Vector3f(), 0.0f, 1.0f, size);
    }
    /**
     * Default constructor if no arguments are provided.
     */
    World(){
        this(new Vector3f(), 0.0f, 1.0f, 1.0f);
    }

    /**
     * Constructor with parameters for position, rotation and scale.
     * @param position the position of the object. An origin vector. Bottom left corner.
     * @param rotation the rotation around the z-axis, in degrees.
     * @param scale the scale multiplier of this object.
     */
    World(Vector3f position, float rotation, float scale, float size){
        super(position, rotation, scale);
        float[] vertices = {
                1.0f * size,    1.0f * size,
                1.0f * size,    0.0f,
                0.0f,           1.0f * size,
                0.0f,           0.0f
        };
        int[] indices = {
                0, 1, 2,
                1, 3, 2
        };
        float[] texcoords = {
                1 * size,   1 * size,
                1 * size,   0.0f,
                0.0f,       1 * size,
                0.0f, 0.0f
        };
        this.setVertexArray(vertices, indices, texcoords);
        this.setShader("src/main/java/shaders/square_shader");
        this.setTexture("src/main/resources/grass.png");
        this.setMatrixLocation();
    }
}