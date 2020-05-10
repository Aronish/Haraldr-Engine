package engine.graphics;

import engine.main.Application;
import engine.main.EntryPoint;
import engine.main.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static engine.main.Application.MAIN_LOGGER;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGB8;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL21.GL_SRGB8;
import static org.lwjgl.opengl.GL21.GL_SRGB8_ALPHA8;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.GL_RG;
import static org.lwjgl.opengl.GL30.GL_RG16F;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glDeleteRenderbuffers;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;
import static org.lwjgl.opengl.GL45.glBindTextureUnit;
import static org.lwjgl.opengl.GL45.glCreateTextures;

@SuppressWarnings("unused")
public class Texture
{
    private static final Shader BRDF_CONVOLUTION = new Shader("default_shaders/brdf_convolution.glsl");

    public static final Texture DEFAULT_WHITE = new Texture(1, 1, new int[] { -1 });
    public static final Texture DEFAULT_BLACK = new Texture(1, 1, new int[] { 0 });
    public static final Texture BRDF_LUT = createBRDFLUT();

    private int width, height;
    private int textureId;

    public Texture(int textureId, int width, int height)
    {
        this.textureId = textureId;
        this.width = width;
        this.height = height;
    }

    private Texture(int width, int height, int[] pixelData)
    {
        this.width = width;
        this.height = height;
        textureId = createTextureFromPixelData(pixelData);
    }

    //DO NOT LOAD .tif!
    public Texture(String path, boolean isColorData)
    {
        textureId = load(path, isColorData);
    }

    private int load(String path, boolean isColorData)
    {
        ByteBuffer image;
        STBImage.stbi_set_flip_vertically_on_load(true);
        int internalFormat, format;
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer comps = stack.mallocInt(1);
            ByteBuffer data = IOUtils.readResource(path, (stream -> IOUtils.resourceToByteBuffer(stream, 2))); //I don't know what this buffer size is
            if (data != null) image = STBImage.stbi_load_from_memory(data, width, height, comps, 0);
            else throw new NullPointerException("Image at " + path + " not found!");

            this.width = width.get();
            this.height = height.get();
            int components = comps.get();
            if (EntryPoint.DEBUG) MAIN_LOGGER.info(String.format("Loaded texture %s | Width: %d, Height: %d, Components: %d", path, this.width, this.height, components));
            switch (components)
            {
                case 1:
                    internalFormat = GL_RED;
                    format = GL_RED;
                    break;
                case 3:
                    internalFormat = isColorData ? GL_SRGB8 : GL_RGB8;
                    format = GL_RGB;
                    break;
                case 4:
                    internalFormat = isColorData ? GL_SRGB8_ALPHA8 : GL_RGBA8;
                    format = GL_RGBA;
                    break;
                default:
                    MAIN_LOGGER.error("Image format not supported!");
                    internalFormat = GL_SRGB8_ALPHA8;
                    format = GL_RGBA;
                    break;
            }
        }
        assert image != null : "Image was somehow null here!";
        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, image);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
        STBImage.stbi_image_free(image);
        return texture;
    }

    private int createTextureFromPixelData(int[] data)
    {
        //RAW FORMAT: (ARGB)
        int[] image = new int[width * height];
        for (int i = 0; i < width * height; i++)
        {
            int a = (data[i] & 0xff000000) >> 24;
            int r = (data[i] & 0xff0000) >> 16;
            int g = (data[i] & 0xff00) >> 8;
            int b = (data[i] & 0xff);
            image[i] = a << 24 | b << 16 | g << 8 | r;
        }
        int result = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, result);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
        return result;
    }

    private static @NotNull Texture createBRDFLUT()
    {
        int size = 512;
        int mappingFrameBuffer = glGenFramebuffers(), depthRenderBuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthRenderBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, size, size);
        glBindFramebuffer(GL_FRAMEBUFFER, mappingFrameBuffer);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthRenderBuffer);
        if (EntryPoint.DEBUG)
        {
            if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) MAIN_LOGGER.error("Cube map mapping framebuffer INCOMPLETE!");
        }

        int brdf = glCreateTextures(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, brdf);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RG16F, size, size, 0, GL_RG, GL_FLOAT, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, brdf, 0);

        glViewport(0, 0, size, size);
        BRDF_CONVOLUTION.bind();
        glClearColor(1f, 1f, 1f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        Renderer3D.SCREEN_QUAD.bind();
        Renderer3D.SCREEN_QUAD.drawElements();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glDeleteFramebuffers(mappingFrameBuffer);
        glDeleteRenderbuffers(depthRenderBuffer);
        glViewport(0, 0, Application.initWidth, Application.initHeight);
        return new Texture(brdf, size, size);
    }

    public int getWidth()
    {
        return width;
    }

    public void bind(int unit)
    {
        glBindTextureUnit(unit, textureId);
    }

    public void unbind(int unit)
    {
        glBindTextureUnit(unit, 0);
    }

    public void delete()
    {
        glDeleteTextures(textureId);
    }
}
