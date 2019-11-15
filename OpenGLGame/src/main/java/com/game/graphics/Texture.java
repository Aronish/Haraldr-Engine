package com.game.graphics;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL45.glCreateTextures;
import static org.lwjgl.opengl.GL45.glTextureStorage2D;
import static org.lwjgl.opengl.GL45.glTextureSubImage2D;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Represents an OpenGL texture.
 */
public class Texture
{
    private int width, height;
    private int texture;

    public Texture(String path, boolean stb)
    {
        if (stb)
        {
            texture = loadSTB(path);
        }else
        {
            texture = load(path);
        }
    }

    /**
     * Reads the pixel data of the texture file and creates an OpenGL texture.
     * Could probably use stb_image.h for cleaner code, but would require weird path recognization.
     * @param path the path of the texture.
     * @return the OpenGL texture ID.
     */
    private int load(String path)
    {
        int[] pixels = null;
        try
        {
            InputStream imageStream = Texture.class.getClassLoader().getResourceAsStream(path);
            if (imageStream == null)
            {
                throw new NullPointerException("Texture not found!");
            }
            BufferedImage image = ImageIO.read(imageStream);
            width = image.getWidth();
            height = image.getHeight();
            pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        int[] data = new int[width * height];
        if (pixels != null)
        {
            for (int i = 0; i < width * height; i++)
            {
                int a = (pixels[i] & 0xff000000) >> 24;
                int r = (pixels[i] & 0xff0000) >> 16;
                int g = (pixels[i] & 0xff00) >> 8;
                int b = (pixels[i] & 0xff);
                data[i] = a << 24 | b << 16 | g << 8 | r;
            }
        }else
        {
            throw new IllegalStateException("Texture was not read and stored properly!");
        }

        int result = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, result);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        //glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
        return result;
    }

    private int loadSTB(String path)
    {
        ByteBuffer data;
        STBImage.stbi_set_flip_vertically_on_load(true);
        try(MemoryStack stack = stackPush())
        {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            IntBuffer pChannels = stack.mallocInt(1);
            ByteBuffer t_data = STBImage.stbi_load(new File(Texture.class.getClassLoader().getResource(path).getFile()).getAbsolutePath(), pWidth, pHeight, pChannels, 0);
            //TODO: Try getting stb_image working.
            width = pWidth.get(0);
            height = pHeight.get(0);
            data = t_data;
        }

        int texture = glCreateTextures(GL_TEXTURE_2D);
        glTextureStorage2D(texture, 0, GL_RGBA8, width, height);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        glTextureSubImage2D(texture, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, data);

        STBImage.stbi_image_free(data);

        return texture;
    }

    int getWidth()
    {
        return width;
    }

    void bind()
    {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    void unbind()
    {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    void delete()
    {
        unbind();
        glDeleteTextures(texture);
    }
}
