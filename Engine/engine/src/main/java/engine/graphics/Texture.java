package engine.graphics;

import engine.main.EntryPoint;
import engine.main.IOUtils;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL45.glBindTextureUnit;

/**
 * Represents an OpenGL texture.
 */
public class Texture
{
    public static final Texture DEFAULT_TEXTURE = new Texture(1, 1, new int[] { -1 });

    private int width, height;
    private int texture;

    public Texture(int width, int height, int[] pixelData)
    {
        this.width = width;
        this.height = height;
        texture = createTexture(pixelData);
    }

    public Texture(String path)
    {
        texture = load(path); // Width and height are set through BufferedImage
    }

    /**
     * Reads the pixel data of the texture file and creates an OpenGL texture.
     * Could probably use stb_image.h for cleaner code, but would require weird path recognization.
     * @param path the path of the texture.
     * @return the OpenGL texture ID.
     */
    private int load(String path)
    {
        int[] pixelData = IOUtils.readResource(path, this::readToIntArray);
        return createTexture(pixelData);
    }

    private int createTexture(int[] pixelData)
    {
        //RAW FORMAT: (ARGB)
        int[] data = new int[width * height];
        if (pixelData != null)
        {
            for (int i = 0; i < width * height; i++)
            {
                int a = (pixelData[i] & 0xff000000) >> 24;
                int r = (pixelData[i] & 0xff0000) >> 16;
                int g = (pixelData[i] & 0xff00) >> 8;
                int b = (pixelData[i] & 0xff);
                data[i] = a << 24 | b << 16 | g << 8 | r;
            }
        }
        else
        {
            throw new IllegalStateException("Texture was not read and stored properly!");
        }

        int result = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, result);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        glBindTexture(GL_TEXTURE_2D, 0);
        return result;
    }

    @Nullable
    private int[] readToIntArray(InputStream inputStream)
    {
        int[] pixels;
        try
        {
            BufferedImage image = ImageIO.read(inputStream);
            width = image.getWidth();
            height = image.getHeight();
            pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);
            return pixels;
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public int getWidth()
    {
        return width;
    }

    public void bind(int textureUnit)
    {
        glBindTextureUnit(textureUnit, texture);
    }

    public void unbind(int textureUnit)
    {
        glBindTextureUnit(textureUnit, 0);
    }

    public void delete()
    {
        glDeleteTextures(texture);
    }
}
