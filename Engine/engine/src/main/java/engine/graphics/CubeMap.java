package engine.graphics;

import engine.main.Application;
import engine.main.EntryPoint;
import engine.main.IOUtils;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static engine.main.Application.MAIN_LOGGER;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
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
    private static final Shader MAP_CUBEMAP = new Shader("default_shaders/map_cubemap.glsl");
    private static final Shader CUBEMAP = new Shader("default_shaders/cubemap.glsl");
    private int size, cubemap;

    public CubeMap(String path)
    {
        cubemap = createCubeMapFromHdr(path);
    }

    public void bind(int unit)
    {
        glBindTextureUnit(unit, cubemap);
    }

    public void unbind(int unit)
    {
        glBindTextureUnit(unit, 0);
    }

    public void renderSkyBox()
    {
        glDepthFunc(GL_LEQUAL);
        glCullFace(GL_FRONT);
        CUBEMAP.bind();
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubemap);
        DefaultModels.CUBE.bind();
        DefaultModels.CUBE.drawElements();
        glDepthFunc(GL_LESS);
        glCullFace(GL_BACK);
    }

    private int createCubeMapFromHdr(String path)
    {
        //Load equirectangular hdr
        int equirectangularHdr = loadHdrImage(path);

        //Framebuffer for mapping to cubemap texture
        int mappingFrameBuffer = glGenFramebuffers(), depthRenderBuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthRenderBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, size, size); //Assuming 2:1 equirectangular
        glBindFramebuffer(GL_FRAMEBUFFER, mappingFrameBuffer);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthRenderBuffer);
        if (EntryPoint.DEBUG)
        {
            if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) MAIN_LOGGER.error("Cube map mapping framebuffer INCOMPLETE!");
        }

        //Allocating memory for cubemap
        int cubemap = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubemap);
        for (int i = 0; i < 6; ++i)
        {
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, size, size, 0, GL_RGB, GL_FLOAT, 0);
        }
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        Matrix4f mappingProjection = Matrix4f.perspective(90f, 1f, 0.1f, 10f);
        Matrix4f[] mappingViews = {
                Matrix4f.lookAt(new Vector3f(), new Vector3f(1f, 0f, 0f), new Vector3f(0f, -1f, 0f)),
                Matrix4f.lookAt(new Vector3f(), new Vector3f(-1f, 0f, 0f), new Vector3f(0f, -1f, 0f)),
                Matrix4f.lookAt(new Vector3f(), new Vector3f(0f, 1f, 0f), new Vector3f(0f, 0f, 1f)),
                Matrix4f.lookAt(new Vector3f(), new Vector3f(0f, -1f, 0f), new Vector3f(0f, 0f, -1f)),
                Matrix4f.lookAt(new Vector3f(), new Vector3f(0f, 0f, 1f), new Vector3f(0f, -1f, 0f)),
                Matrix4f.lookAt(new Vector3f(), new Vector3f(0f, 0f, -1f), new Vector3f(0f, -1f, 0f)),
        };
        //Render cube faces to cubemap texture
        MAP_CUBEMAP.bind();
        MAP_CUBEMAP.setMatrix4f(Matrix4f.scale(new Vector3f(0.5f)), "model"); //(Cube .obj is two units in size)
        MAP_CUBEMAP.setMatrix4f(mappingProjection, "mappingProjection");
        glBindTexture(GL_TEXTURE_2D, equirectangularHdr);
        glViewport(0, 0, size, size);
        glBindFramebuffer(GL_FRAMEBUFFER, mappingFrameBuffer);
        glClearColor(1f, 1f, 1f, 1f);
        glCullFace(GL_FRONT);
        for (int i = 0; i < 6; ++i)
        {
            MAP_CUBEMAP.setMatrix4f(mappingViews[i], "mappingView");
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, cubemap, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            DefaultModels.CUBE.bind();
            DefaultModels.CUBE.drawElements();
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glDeleteBuffers(mappingFrameBuffer);
        glDeleteBuffers(depthRenderBuffer);
        glDeleteTextures(equirectangularHdr);
        glViewport(0, 0, Application.initWidth, Application.initHeight);
        glCullFace(GL_BACK);
        return cubemap;
    }

    private int loadHdrImage(String path)
    {
        FloatBuffer image;
        STBImage.stbi_set_flip_vertically_on_load(true);
        int width1;
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer comps = stack.mallocInt(1);
            ByteBuffer data = IOUtils.readResource(path, (stream) -> IOUtils.resourceToByteBuffer(stream, 4 * 4096)); //Assuming hdr format as 4 8bit channels
            if (data != null) image = STBImage.stbi_loadf_from_memory(data, width, height, comps, 0);
            else throw new NullPointerException("Resource at " + path + " not found!");

            width1 = width.get();
            this.size = height.get();
            if (EntryPoint.DEBUG) MAIN_LOGGER.info(String.format("Loaded HDR %s | Width: %d, Height: %d, Components: %d", path, width1, this.size, comps.get()));
        }
        assert image != null : "Image was somehow null here!";
        int hdr = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, hdr);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB16F, width1, size, 0, GL_RGB, GL_FLOAT, image);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);
        STBImage.stbi_image_free(image);
        return hdr;
    }

    public void delete()
    {
        glDeleteTextures(cubemap);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CubeMap cubeMap = (CubeMap) o;
        return cubemap == cubeMap.cubemap;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(cubemap);
    }
}