package main.java;

import main.java.graphics.Models;
import main.java.math.Vector3f;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main world object, on which everything should be located to be inside the world.
 */
class World extends Entity {

    private ArrayList<Obstacle> obstacles;

    World(){
        this(new Vector3f(), 0.0f, 1.0f);
    }

    /**
     * Constructor with parameters for position, rotation and scale.
     * @param position the position of the object. An origin vector. Bottom left corner.
     * @param rotation the rotation around the z-axis, in degrees.
     * @param scale the scale multiplier of this object.
     */
    private World(Vector3f position, float rotation, float scale){
        super(Models.getWORLD(), position, rotation, scale);
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
                    this.obstacles.add(new Obstacle(new Vector3f(x, y)));
                }
            }
        }
    }

    ArrayList<Obstacle> getObstacles(){
        return this.obstacles;
    }
}