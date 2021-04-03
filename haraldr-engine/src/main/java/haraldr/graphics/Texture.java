package haraldr.graphics;

import haraldr.debug.Logger;
import haraldr.ui.Font;
import haraldr.main.EntryPoint;
import haraldr.main.IOUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.EXTTextureCompressionS3TC;
import org.lwjgl.opengl.EXTTextureSRGB;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
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

@SuppressWarnings({"unused", "WeakerAccess"})
public class Texture
{
    private static final VertexArray SCREEN_QUAD = new VertexArray();
    private static final Shader BRDF_CONVOLUTION = Shader.create("internal_shaders/brdf_convolution.glsl");

    public static final Texture DEFAULT_WHITE = new Texture(1, 1, new int[] { -1 });
    public static final Texture DEFAULT_BLACK = new Texture(1, 1, new int[] { 0 });
    public static final Texture BRDF_LUT;

    private int width, height;
    private int textureHandle;

    static
    {
        ResourceManager.addTexture("DEFAULT_BLACK", DEFAULT_BLACK);
        ResourceManager.addTexture("DEFAULT_WHITE", DEFAULT_WHITE);
        float[] quadVertexData = {
                -1f,  1f,   0f, 1f,
                1f,  1f,   1f, 1f,
                1f, -1f,   1f, 0f,
                -1f, -1f,   0f, 0f
        };
        VertexBuffer quadVertices = new VertexBuffer(
                quadVertexData,
                new VertexBufferLayout(new VertexBufferElement(ShaderDataType.FLOAT2), new VertexBufferElement(ShaderDataType.FLOAT2)),
                VertexBuffer.Usage.STATIC_DRAW
        );
        SCREEN_QUAD.setVertexBuffers(quadVertices);
        SCREEN_QUAD.setIndexBufferData(new int[] { 0, 3, 2, 0, 2, 1 });
        BRDF_LUT = createBRDFLUT();
    }

    public static void init()
    {
        ResourceManager.addTexture("BRDF_LUT", BRDF_LUT);
    }

    @Contract(pure = true)
    private Texture() {}

    public static Texture wrapTextureHandle(String prefix, int fontAtlasHandle)
    {
        String key = prefix + fontAtlasHandle;
        if (ResourceManager.isTextureLoaded(key))
        {
            return ResourceManager.getLoadedTexture(key);
        }
        else
        {
            Texture texture = new Texture();
            texture.textureHandle = fontAtlasHandle;
            texture.width = Font.WIDTH;
            texture.height = Font.HEIGHT;
            ResourceManager.addTexture(key, texture);
            return texture;
        }
    }

    public static Texture create(String path, boolean isColorData)
    {
        String key = path + "_" + isColorData;
        if (ResourceManager.isTextureLoaded(key))
        {
            return ResourceManager.getLoadedTexture(key);
        }
        else
        {
            Texture texture = load(path, isColorData);
            ResourceManager.addTexture(key, texture);
            return texture;
        }
    }

    private static @NotNull Texture load(String path, boolean isColorData)
    {
        Texture result = new Texture();

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

            result.width = width.get();
            result.height = height.get();
            int components = comps.get();
            Logger.info(String.format("Loaded texture %s | Width: %d, Height: %d, Components: %d", path, result.width, result.height, components));
            switch (components)
            {
                case 1 -> {
                    internalFormat = GL_RED;
                    format = GL_RED;
                }
                case 3 -> {
                    internalFormat = isColorData ? EXTTextureSRGB.GL_COMPRESSED_SRGB_S3TC_DXT1_EXT : EXTTextureCompressionS3TC.GL_COMPRESSED_RGB_S3TC_DXT1_EXT;
                    format = GL_RGB;
                }
                case 4 -> {
                    internalFormat = isColorData ? EXTTextureSRGB.GL_COMPRESSED_SRGB_ALPHA_S3TC_DXT5_EXT : EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT;
                    format = GL_RGBA;
                }
                default -> {
                    Logger.error("Image format not supported!");
                    internalFormat = GL_SRGB8_ALPHA8;
                    format = GL_RGBA;
                }
            }
        }
        assert image != null : "Image was somehow null here!";
        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, result.width, result.height, 0, format, GL_UNSIGNED_BYTE, image);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
        STBImage.stbi_image_free(image);

        result.textureHandle = texture;
        return result;
    }

    private Texture(int textureHandle, int width, int height)
    {
        this.textureHandle = textureHandle;
        this.width = width;
        this.height = height;
    }

    private Texture(int width, int height, int[] pixelData)
    {
        this.width = width;
        this.height = height;
        textureHandle = createTextureFromPixelData(pixelData);
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

    @Contract(" -> new")
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
            if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) Logger.error("Cube map mapping framebuffer INCOMPLETE!");
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
        SCREEN_QUAD.bind();
        SCREEN_QUAD.drawElements();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glDeleteFramebuffers(mappingFrameBuffer);
        glDeleteRenderbuffers(depthRenderBuffer);
        return new Texture(brdf, size, size);
    }

    public int getWidth()
    {
        return width;
    }

    public void bind(int unit)
    {
        glBindTextureUnit(unit, textureHandle);
    }

    public void unbind(int unit)
    {
        glBindTextureUnit(unit, 0);
    }

    public void delete()
    {
        glDeleteTextures(textureHandle);
    }

    public static void dispose()
    {
        SCREEN_QUAD.delete();
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Texture texture = (Texture) o;
        return textureHandle == texture.textureHandle;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(textureHandle);
    }
}
