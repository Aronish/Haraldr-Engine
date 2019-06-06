package main.java;

import main.java.math.SimplexNoise;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class NoiseTest {

    private static int w = 1280, h = 720;
    private static double scale = 0.001d;

    public static void main(String[] args){
        new SimplexNoise();
        Random random = new Random();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int noise;
        float z = 0;
        for (float i = 0; i < w; ++i){
            for (float j = 0; j < h; ++j){
                noise = (int) ((SimplexNoise.noise(i * scale, j * scale, z * scale) + 1) / 2 * 255);
                //System.out.println(noise);
                //image.setRGB((int) i, (int) j, new Color(0, noise > 128 ? 200 : 80, noise < 128 ? 200 : 80, 255).getRGB());
                image.setRGB((int) i, (int) j, new Color(0, 0, 0, noise).getRGB());
                z += 0.005f;
            }
        }
        File outFile = new File("test/main/resources/noise.png");
        try{
            ImageIO.write(image, "png", outFile);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
