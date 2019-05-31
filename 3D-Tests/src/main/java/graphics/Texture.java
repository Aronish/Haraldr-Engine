package main.java.graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * Class for handling the creation and loading of textures into OpenGl.
 */
class Texture {

    private int width, height;
    private int texture;

    /**
     * Constructor for the texture.
     * @param path the path of the texture.
     */
    Texture(String path){
        texture = load(path);
    }

    /**
     * Reads the pixel data of the picture and creates an OpenGL texture.
     * @param path the path of the texture.
     * @return the OpenGL texture ID.
     */
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
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
        return result;
    }

    /**
     * Binds this texture for drawing.
     */
    void bind(){
        glBindTexture(GL_TEXTURE_2D, this.texture);
    }

    /**
     * Unbinds this texture to avoid weird conflicts.
     */
    void unbind(){
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
