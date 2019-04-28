package main.java;

import main.java.graphics.TexturedModel;
import main.java.math.Vector3f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main world object, on which everything should be located to be inside the world.
 */
class World extends TexturedModel {

    private ArrayList<Obstacle> obstacles;

    /**
     * Default constructor if no arguments are provided.
     */
    World(){
        this(new Vector3f(), 0.0f, 1.0f, 1.0f);
    }

    /**
     * Constructor with one parameter for the size
     * @param size the size of the world.
     */
    World(float size){
        this(new Vector3f(), 0.0f, 1.0f, size);
    }

    World(Vector3f position, float size){
        this(position, 0.0f, 1.0f, size);
    }

    /**
     * Constructor with parameters for position, rotation and scale.
     * @param position the position of the object. An origin vector. Bottom left corner.
     * @param rotation the rotation around the z-axis, in degrees.
     * @param scale the scale multiplier of this object.
     * @param size the size of the world.
     */
    private World(Vector3f position, float rotation, float scale, float size){
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
                1.0f * size,    0.0f,
                1.0f * size,    1.0f * size,
                0.0f,           0.0f,
                0.0f,           1.0f * size
        };
        this.setVertexArray(vertices, indices, texcoords);
        this.setShader("src/main/java/shaders/square_shader");
        this.setTexture("src/main/resources/grass.png");
        this.setMatrixLocation();
        this.obstacles = new ArrayList<>();
        generateWorld("src/main/resources/pixel_test_2.png");
    }

    private void generateWorld(String path){
        int width = 0, height = 0;
        BufferedImage image = null;
        try{
            image = ImageIO.read(new FileInputStream(path));
            width = image.getWidth();
            height = image.getHeight();
        }catch (IOException e){
            e.printStackTrace();
        }
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                Color color = new Color(image.getRGB(x, y));
                if (color.getRed() == 255){
                    this.obstacles.add(new Obstacle(new Vector3f(x, y, 0.0f)));
                }
            }
        }
    }

    ArrayList<Obstacle> getObstacles(){
        return this.obstacles;
    }
}