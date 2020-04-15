package engine.graphics;

import engine.main.Application;
import engine.main.IOUtils;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGB8;
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
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH24_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.GL_RGB16F;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;
import static org.lwjgl.opengl.GL45.glBindTextureUnit;

public class CubeMap
{
    private static final Shader MAP_CUBEMAP = new Shader("default_shaders/map_cubemap.vert", "default_shaders/map_cubemap.frag");
    private static final Shader CUBEMAP = new Shader("default_shaders/cubemap.vert", "default_shaders/cubemap.frag");
    private int width, height, texture, cubemap, texture2;

    public CubeMap(String path)
    {
        cubemap = loadHdr(path);
    }

    public int test()
    {
        return texture;
    }

    public void renderSkyBox()
    {
        glDepthFunc(GL_LEQUAL);
        CUBEMAP.bind();
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubemap);
        DefaultModels.CUBE.bind();
        DefaultModels.CUBE.drawElements();
        glDepthFunc(GL_LESS);
    }

    public void delete()
    {
        glDeleteTextures(texture);
        glDeleteTextures(cubemap);
    }

    private int loadHdr(String path)
    {
        //Load equirectangular hdr
        FloatBuffer data = readToFloatArray(path);
        int result = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, result);
        System.out.println(width + " " + height);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB16F, width, height, 0, GL_RGB, GL_FLOAT, data);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);
        texture = result;
        STBImage.stbi_image_free(data);

        //Framebuffer for mapping to cubemap texture
        int captureFBO = glGenFramebuffers(), captureRBO = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, captureRBO);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, 1024, 1024);
        glBindFramebuffer(GL_FRAMEBUFFER, captureFBO);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, captureRBO);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) System.out.println("INCOMPLETE");
        else System.out.println("COMPLETE");

        //Allocating memory for cubemap
        int cubemap = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubemap);
        for (int i = 0; i < 6; ++i)
        {
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, 1024, 1024, 0, GL_RGB, GL_FLOAT, 0);
        }
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        Matrix4f captureProjection = Matrix4f.perspective(90f, 1f, 0.1f, 10f);
        Matrix4f[] captureViews = {
                Matrix4f.lookAt(new Vector3f(), new Vector3f(1f, 0f, 0f), new Vector3f(0f, -1f, 0f)),
                Matrix4f.lookAt(new Vector3f(), new Vector3f(-1f, 0f, 0f), new Vector3f(0f, -1f, 0f)),
                Matrix4f.lookAt(new Vector3f(), new Vector3f(0f, 1f, 0f), new Vector3f(0f, 0f, 1f)),
                Matrix4f.lookAt(new Vector3f(), new Vector3f(0f, -1f, 0f), new Vector3f(0f, 0f, -1f)),
                Matrix4f.lookAt(new Vector3f(), new Vector3f(0f, 0f, 1f), new Vector3f(0f, -1f, 0f)),
                Matrix4f.lookAt(new Vector3f(), new Vector3f(0f, 0f, -1f), new Vector3f(0f, -1f, 0f)),
        };

        MAP_CUBEMAP.bind();
        MAP_CUBEMAP.setMatrix4f(Matrix4f.scale(new Vector3f(0.5f)), "model");
        MAP_CUBEMAP.setMatrix4f(captureProjection, "captureProjection");
        glBindTexture(GL_TEXTURE_2D, texture);
        glViewport(0, 0, 1024, 1024);
        glBindFramebuffer(GL_FRAMEBUFFER, captureFBO);
        glClearColor(1f, 1f, 1f, 1f);
        for (int i = 0; i < 6; ++i)
        {
            MAP_CUBEMAP.setMatrix4f(captureViews[i], "captureView");
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, cubemap, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            DefaultModels.CUBE.bind();
            DefaultModels.CUBE.drawElements();
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, 1280, 720);
        return cubemap;
    }

    private FloatBuffer readToFloatArray(String path)
    {
        FloatBuffer image = null;
        STBImage.stbi_set_flip_vertically_on_load(true);
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer width = stack.callocInt(1);
            IntBuffer height = stack.callocInt(1);
            IntBuffer comps = stack.callocInt(1);
            ByteBuffer data = ioResourceToByteBuffer(path, 32 * 1024);
            /*if (!STBImage.stbi_info_from_memory(data, width, height, comps))
            {
                System.out.println("FAILED" + STBImage.stbi_failure_reason());
            }
            else
            {
                System.out.println("Worked " + STBImage.stbi_failure_reason());
                System.out.println(width.get() + " " + height.get() + " " + comps.get());
            }*/
            image = STBImage.stbi_loadf_from_memory(data, width, height, comps, 0);
            if (image == null) System.out.println("Was Null!");
            this.width = width.get();
            this.height = height.get();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return image;
    }

    private static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException
    {
        return IOUtils.readResource(resource, CubeMap::readDataTest);
    }

    private static @NotNull ByteBuffer readDataTest(InputStream data)
    {
        ByteBuffer buffer = null;
        try
        {
            ReadableByteChannel rbc = Channels.newChannel(data);
            buffer = createByteBuffer(32 * 1024);
            while (true) {
                int bytes = rbc.read(buffer);
                if (bytes == -1)
                {
                    break;
                }
                if (buffer.remaining() == 0)
                {
                    buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                }
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        buffer.flip();
        return buffer;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
}