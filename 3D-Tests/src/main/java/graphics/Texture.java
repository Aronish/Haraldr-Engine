package main.java.graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import static org.lwjgl.opengl.GL46.*;

class Texture {

    private int width, height;
    private int texture;

    Texture(String path){
        texture = load(path);
    }

    private int load(String path){
        int[] pixels = null;
        try{
            BufferedImage image = ImageIO.read(new FileInputStream(path));
            this.width = image.getWidth();
            this.height = image.getHeight();
            pixels = new int[this.width * this.height];
            image.getRGB(0, 0, this.width, this.height, pixels, 0, this.width);
        }catch (IOException e){
            e.printStackTrace();
        }

        int[] data = new int[this.width * this.height];
        for (int i = 0; i < this.width * this.height; i++) {
            int a = (pixels[i] & 0xff000000) >> 24;
            int r = (pixels[i] & 0xff0000) >> 16;
            int g = (pixels[i] & 0xff00) >> 8;
            int b = (pixels[i] & 0xff);
            data[i] = a << 24 | b << 16 | g << 8 | r;
        }

        int result = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, result);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        glBindTexture(GL_TEXTURE_2D, 0);
        return result;
    }

    void bind(){
        glBindTexture(GL_TEXTURE_2D, this.texture);
    }

    void unbind(){
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
