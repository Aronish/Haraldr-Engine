package haraldr.graphics;

import haraldr.debug.Logger;
import haraldr.main.EntryPoint;
import haraldr.main.IOUtils;
import haraldr.math.Matrix4f;
import haraldr.math.Vector3f;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyexr.EXRHeader;
import org.lwjgl.util.tinyexr.EXRImage;
import org.lwjgl.util.tinyexr.EXRVersion;
import org.lwjgl.util.tinyexr.TinyEXR;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_ALWAYS;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
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
import static org.lwjgl.opengl.GL11.glStencilFunc;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTexSubImage2D;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.GL_RGB16F;
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
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL45.glBindTextureUnit;
import static org.lwjgl.opengl.GL45.glCreateTextures;
import static org.lwjgl.util.tinyexr.TinyEXR.TINYEXR_SUCCESS;

public class CubeMap
{
    private static final Shader MAP_CUBEMAP = Shader.create("internal_shaders/map_cubemap.glsl");
    private static final Shader SKYBOX      = Shader.create("default_shaders/skybox.glsl");

    private int cubeMapId;

    @Contract(pure = true)
    private CubeMap(int cubeMapId)
    {
        this.cubeMapId = cubeMapId;
    }

    public void bind(int unit)
    {
        glBindTextureUnit(unit, cubeMapId);
    }

    public void unbind(int unit)
    {
        glBindTextureUnit(unit, 0);
    }

    public void renderSkyBox()
    {
        glDepthFunc(GL_LEQUAL);
        glCullFace(GL_FRONT);
        glStencilFunc(GL_ALWAYS, 0, -1);
        SKYBOX.bind();
        glBindTextureUnit(0, cubeMapId);
        DefaultModels.CUBE.bind();
        DefaultModels.CUBE.drawElements();
        glDepthFunc(GL_LESS);
        glCullFace(GL_BACK);
    }

    public static @NotNull CubeMap createEnvironmentMap(String path) // Will still map an equirectangular map. Reduces disk usage.
    {
        if (ResourceManager.isCubeMapLoaded(path))
        {
            return ResourceManager.getLoadedCubeMap(path);
        }
        else
        {
            //Read equirectangular HDR image
            FloatBuffer image;
            STBImage.stbi_set_flip_vertically_on_load(true);
            int width1, size;
            try (MemoryStack stack = MemoryStack.stackPush())
            {
                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);
                IntBuffer comps = stack.mallocInt(1);
                ByteBuffer data = IOUtils.readResource(path, (stream) -> IOUtils.resourceToByteBuffer(stream, 4 * 4096)); //Assuming hdr format as 4 8bit channels
                if (data != null) image = STBImage.stbi_loadf_from_memory(data, width, height, comps, 0);
                else throw new NullPointerException("Resource at " + path + " not found!");

                width1 = width.get();
                size = height.get();
                Logger.info(String.format("Loaded HDR %s | Width: %d, Height: %d, Components: %d", path, width1, size, comps.get()));
            }
            assert image != null : "Image was somehow null here!";
            int equirectangular = glCreateTextures(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, equirectangular);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB16F, width1, size, 0, GL_RGB, GL_FLOAT, image);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            STBImage.stbi_image_free(image);

            //Create framebuffer for mapping
            int mappingFrameBuffer = glGenFramebuffers(), depthRenderBuffer = glGenRenderbuffers();
            glBindRenderbuffer(GL_RENDERBUFFER, depthRenderBuffer);
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, size, size); //Assuming 2:1 equirectangular
            glBindFramebuffer(GL_FRAMEBUFFER, mappingFrameBuffer);
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthRenderBuffer);
            if (EntryPoint.DEBUG)
            {
                if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) Logger.error("Cube map mapping framebuffer INCOMPLETE!");
            }

            //Allocating memory for cubemap
            int cubeMap = glCreateTextures(GL_TEXTURE_CUBE_MAP);
            glBindTexture(GL_TEXTURE_CUBE_MAP, cubeMap);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            for (int i = 0; i < 6; ++i)
            {
                glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, size, size, 0, GL_RGB, GL_FLOAT, 0);
            }
            glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
            //A list of views for all cube faces
            Matrix4f mappingProjection = Matrix4f.perspective(90f, 1f, 0.1f, 10f);
            Matrix4f[] mappingViews = {
                    Matrix4f.lookAt(new Vector3f(), new Vector3f(1f, 0f, 0f), new Vector3f(0f, -1f, 0f)),
                    Matrix4f.lookAt(new Vector3f(), new Vector3f(-1f, 0f, 0f), new Vector3f(0f, -1f, 0f)),
                    Matrix4f.lookAt(new Vector3f(), new Vector3f(0f, 1f, 0f), new Vector3f(0f, 0f, 1f)),
                    Matrix4f.lookAt(new Vector3f(), new Vector3f(0f, -1f, 0f), new Vector3f(0f, 0f, -1f)),
                    Matrix4f.lookAt(new Vector3f(), new Vector3f(0f, 0f, 1f), new Vector3f(0f, -1f, 0f)),
                    Matrix4f.lookAt(new Vector3f(), new Vector3f(0f, 0f, -1f), new Vector3f(0f, -1f, 0f)),
            };
            //Render textured cube faces to cubemap color attachment texture
            MAP_CUBEMAP.bind();
            MAP_CUBEMAP.setMatrix4f("model", Matrix4f.identity().scale(new Vector3f(0.5f))); //(Cube .obj is two units in size)
            MAP_CUBEMAP.setMatrix4f("mappingProjection", mappingProjection);

            glViewport(0, 0, size, size);
            glClearColor(1f, 1f, 1f, 1f);
            glCullFace(GL_FRONT);
            glBindTexture(GL_TEXTURE_CUBE_MAP, cubeMap);
            for (int i = 0; i < 6; ++i)
            {
                MAP_CUBEMAP.setMatrix4f("mappingView", mappingViews[i]);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, cubeMap, 0);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                DefaultModels.CUBE.bind();
                DefaultModels.CUBE.drawElements();
            }
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glDeleteFramebuffers(mappingFrameBuffer);
            glDeleteRenderbuffers(depthRenderBuffer);
            glDeleteTextures(equirectangular);
            glCullFace(GL_BACK);

            glGenerateMipmap(GL_TEXTURE_CUBE_MAP);

            CubeMap environmentMap = new CubeMap(cubeMap);
            ResourceManager.addCubeMap(path, environmentMap);
            Logger.info("Generated environment map from " + path);
            return environmentMap;
        }
    }

    public static @NotNull CubeMap createDiffuseIrradianceMap(String path)
    {
        if (ResourceManager.isCubeMapLoaded(path))
        {
            return ResourceManager.getLoadedCubeMap(path);
        }
        else
        {
            CubeMap diffuseIrradianceMap = loadEXRCubemap(path, (exrData ->
            {
                //Create OpenGL cubemap
                float[][] faceData = new float[6][exrData.faceSize * exrData.height * 3];

                int lineOffset = exrData.faceSize * 3;
                for (int i = 0; i < exrData.width * exrData.height; ++i)
                {
                    int face    = i / exrData.faceSize % 6;
                    int index   = i % exrData.faceSize;
                    int line    = i / (exrData.faceSize * 6);
                    faceData[face][3 * index + line * lineOffset]       = exrData.channelData[2].get(i);
                    faceData[face][3 * index + line * lineOffset + 1]   = exrData.channelData[1].get(i);
                    faceData[face][3 * index + line * lineOffset + 2]   = exrData.channelData[0].get(i);
                }

                int cubeMap = glCreateTextures(GL_TEXTURE_CUBE_MAP);
                glBindTexture(GL_TEXTURE_CUBE_MAP, cubeMap);
                for (int face = 0; face < 6; ++face)
                {
                    glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + face, 0, GL_RGB16F, exrData.faceSize, exrData.faceSize, 0, GL_RGB, GL_FLOAT, faceData[face]);
                }
                glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

                return new CubeMap(cubeMap);
            }));
            ResourceManager.addCubeMap(path, diffuseIrradianceMap);
            Logger.info("Loaded diffuse irradiance map " + path);
            return diffuseIrradianceMap;
        }
    }

    public static @NotNull CubeMap createPrefilteredEnvironmentMap(String path)
    {
        if (ResourceManager.isCubeMapLoaded(path))
        {
            return ResourceManager.getLoadedCubeMap(path);
        }
        else
        {
            CubeMap prefilteredEnvironmentMap = loadEXRCubemap(path, (exrData ->
            {
                //Compute mip levels
                int mipLevels = 0;
                for (int currentHeight = 0; currentHeight < exrData.height;)
                {
                    currentHeight += (float) exrData.faceSize * Math.pow(0.5f, mipLevels);
                    ++mipLevels;
                }

                //Create OpenGL cubemap
                float[][][] faceData = new float[mipLevels][6][exrData.faceSize * exrData.height * 3];

                int mipLevelPixelOffset = 0;
                for (int mipLevel = 0; mipLevel < mipLevels; ++mipLevel)
                {
                    int mipLevelSize = (int) (exrData.faceSize * Math.pow(0.5f, mipLevel));
                    int lineOffset = mipLevelSize * 3;
                    int mipLineOffset = exrData.width - 6 * mipLevelSize;

                    for (int i = 0, pos = mipLevelPixelOffset; i < 6 * mipLevelSize * mipLevelSize; ++i)
                    {
                        int face    = i / mipLevelSize % 6;
                        int index   = i % mipLevelSize;
                        int line    = i / (mipLevelSize * 6);
                        faceData[mipLevel][face][3 * index + line * lineOffset]     = exrData.channelData[2].get(pos);
                        faceData[mipLevel][face][3 * index + line * lineOffset + 1] = exrData.channelData[1].get(pos);
                        faceData[mipLevel][face][3 * index + line * lineOffset + 2] = exrData.channelData[0].get(pos);
                        if (mipLevel > 0 && ((i % (6 * mipLevelSize)) + 1) / (6 * mipLevelSize) == 1) pos += mipLineOffset + 1;
                        else ++pos;
                    }
                    mipLevelPixelOffset += exrData.width * mipLevelSize;
                }

                int cubeMap = glCreateTextures(GL_TEXTURE_CUBE_MAP);
                glBindTexture(GL_TEXTURE_CUBE_MAP, cubeMap);
                glTexStorage2D(GL_TEXTURE_CUBE_MAP, mipLevels, GL_RGB16F, exrData.faceSize, exrData.faceSize);
                for (int mipLevel = 0; mipLevel < mipLevels; ++mipLevel)
                {
                    int mipLevelSize = (int) (exrData.faceSize * Math.pow(0.5f, mipLevel));
                    for (int face = 0; face < 6; ++face)
                    {
                        glTexSubImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + face, mipLevel, 0, 0, mipLevelSize, mipLevelSize, GL_RGB, GL_FLOAT, faceData[mipLevel][face]);
                    }
                }
                glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
                glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

                return new CubeMap(cubeMap);
            }));

            ResourceManager.addCubeMap(path, prefilteredEnvironmentMap);
            Logger.info("Loaded prefiltered environment map " + path);
            return prefilteredEnvironmentMap;
        }
    }

    /**
     * Loads Haraldr's custom cubemap file and creates a cube map from it.
     * @param loader a function which uses the raw loaded data to produce a cube map.
     */
    private static CubeMap loadEXRCubemap(String path, CubeMapLoader loader)
    {
        EXRVersion exr_version = EXRVersion.create();
        EXRHeader exr_header = EXRHeader.create();
        TinyEXR.InitEXRHeader(exr_header);

        ByteBuffer exrMemoryData = IOUtils.readResource(path, (stream) -> IOUtils.resourceToByteBuffer(stream, 2));

        try (MemoryStack stack = MemoryStack.stackPush())
        {
            PointerBuffer err = stack.mallocPointer(1);
            int resultCode = TinyEXR.ParseEXRHeaderFromMemory(exr_header, exr_version, exrMemoryData, err);
            if (resultCode != TINYEXR_SUCCESS)
            {
                throw new RuntimeException("EXR parse error: " + err.get(0));
            }
        }

        for (int i = 0; i < exr_header.num_channels(); i++)
        {
            if (exr_header.pixel_types().get(i) == TinyEXR.TINYEXR_PIXELTYPE_FLOAT) //Maybe HALF?
            {
                exr_header.requested_pixel_types().put(i, TinyEXR.TINYEXR_PIXELTYPE_FLOAT);
            }
        }

        EXRImage exr_image = EXRImage.create();
        TinyEXR.InitEXRImage(exr_image);

        try (MemoryStack stack = MemoryStack.stackPush())
        {
            PointerBuffer err = stack.mallocPointer(1);
            int resultCode = TinyEXR.LoadEXRImageFromMemory(exr_image, exr_header, exrMemoryData, err);
            if (resultCode != TINYEXR_SUCCESS)
            {
                throw new RuntimeException("EXR load errror: " + err.get(0));
            }
        }
        PointerBuffer images = exr_image.images();
        int width = exr_image.width();
        int height = exr_image.height();

        assert images != null;
        FloatBuffer[] channelData = {
                images.getFloatBuffer(0, width * height),   //B
                images.getFloatBuffer(1, width * height),   //G
                images.getFloatBuffer(2, width * height)    //R
        };

        CubeMap cubeMap = loader.load(new EXRData(channelData, width, height));

        TinyEXR.FreeEXRHeader(exr_header);
        TinyEXR.FreeEXRImage(exr_image);
        return cubeMap;
    }

    public void delete()
    {
        glDeleteTextures(cubeMapId);
    }

    private static class EXRData
    {
        private final FloatBuffer[] channelData;
        private int width, height, faceSize;

        private EXRData(FloatBuffer[] channelData, int width, int height)
        {
            this.channelData = channelData;
            this.width = width;
            this.height = height;
            this.faceSize = width / 6;
        }
    }

    @FunctionalInterface
    private interface CubeMapLoader
    {
        CubeMap load(EXRData exrData);
    }
}