package main.java;

import main.java.graphics.TexturedModel;
import main.java.math.Matrix4f;
import main.java.math.Vector3f;

/**
 * The player that is visible in the center of the screen.
 * This is a static model and stays in place.
 */
class Player extends TexturedModel {

    /**
     * Default constructor if no arguments are provided.
     */
    Player(){
        this(new Vector3f(), 0.0f, 1.0f);
    }

    /**
     * Constructor with parameters for position, rotation and scale.
     * @param position the position of the object. An origin vector. Bottom left corner.
     * @param rotation the rotation around the z-axis, in degrees.
     * @param scale the scale multiplier of this object.
     */
    Player(Vector3f position, float rotation, float scale){
        super(position, rotation, scale);
        float[] vertices = {
                1f, 1f,
                1f, 0f,
                0f, 1f,
                0f, 0f
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
        this.setVertexArray(vertices, indices, texcoords);
        this.setShader("src/main/java/shaders/player_shader");
        this.setTexture("src/main/resources/player.png");
        this.setMatrixLocation();
    }

    /**
     * An overridden method that sets a new matrix without the view matrix of the camera included.
     */
    @Override
    protected void updateMatrix() {
        this.matrix = new Matrix4f().MP(this.position, this.rotation, this.scale);
    }
}