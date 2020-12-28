package offlinerenderer;

import haraldr.debug.Logger;
import haraldr.graphics.DefaultModels;
import haraldr.graphics.Shader;
import haraldr.main.EntryPoint;
import haraldr.main.IOUtils;
import haraldr.math.Matrix4f;
import haraldr.math.Vector3f;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyexr.EXRChannelInfo;
import org.lwjgl.util.tinyexr.EXRHeader;
import org.lwjgl.util.tinyexr.EXRImage;
import org.lwjgl.util.tinyexr.TinyEXR;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
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
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
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
import static org.lwjgl.opengl.GL45.glBindTextureUnit;
import static org.lwjgl.opengl.GL45.glCreateTextures;

public class CubeMapGenerator
{
    private static final Shader MAP_DIFFUSE_IRRADIANCE  = Shader.create("internal_shaders/map_diffuse_irradiance.glsl");
    private static final Shader PREFILTER_CONVOLUTION   = Shader.create("internal_shaders/prefilter_convolution.glsl");
    private static final Shader MAP_CUBEMAP             = Shader.create("internal_shaders/map_cubemap.glsl");

    private static Map<String, GeneratedCubeMap> generatedCubeMaps = new HashMap<>();

    public static GeneratedCubeMap createEnvironmentMap(String path)
    {
        if (generatedCubeMaps.containsKey(path))
        {
            return generatedCubeMaps.get(path);
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
                ByteBuffer data = IOUtils.readFile(path, (stream) -> IOUtils.resourceToByteBuffer(stream, 4 * 4096)); //Assuming hdr format as 4 8bit channels
                if (data != null) image = STBImage.stbi_loadf_from_memory(data, width, height, comps, 0);
                else throw new NullPointerException("Resource at " + path + " not found!");

                width1 = width.get();
                size = height.get();
                Logger.info(String.format("Loaded HDR %s | Width: %d, Height: %d, Components: %d", path, width1, size, comps.get()));
            }
            assert image != null : "Image was somehow null here!";
            int texture = glCreateTextures(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, texture);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB16F, width1, size, 0, GL_RGB, GL_FLOAT, image);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            STBImage.stbi_image_free(image);

            GeneratedCubeMap cubeMap = new GeneratedCubeMap(mapToCubeMap(MAP_CUBEMAP, texture, size, true,
            (Shader mappingShader, int framebuffer, int depthRenderBuffer, int colorAttachment, int textureToMap, Matrix4f[] mappingViews, int cubeFaceSize) ->
            {
                for (int i = 0; i < 6; ++i)
                {
                    mappingShader.setMatrix4f("mappingView", mappingViews[i]);
                    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, colorAttachment, 0);
                    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                    DefaultModels.CUBE.bind();
                    DefaultModels.CUBE.drawElements();
                }
            }), size, 0, path);
            glBindTexture(GL_TEXTURE_CUBE_MAP, cubeMap.getCubeMapId());
            glGenerateMipmap(GL_TEXTURE_CUBE_MAP); // Do this after having set the textures. Used by other pbr maps.
            glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
            glDeleteTextures(texture);

            generatedCubeMaps.put(path, cubeMap);
            Logger.info(String.format("Generated environment map from %s", path));
            return cubeMap;
        }
    }

    public static @NotNull GeneratedCubeMap createDiffuseIrradianceMap(@NotNull GeneratedCubeMap environmentMap, int size)
    {
        String name = "DIFF_IRR_" + environmentMap.getCubeMapId();
        if (generatedCubeMaps.containsKey(name))
        {
            return generatedCubeMaps.get(name);
        }
        else
        {
            GeneratedCubeMap cubeMap = new GeneratedCubeMap(mapToCubeMap(MAP_DIFFUSE_IRRADIANCE, environmentMap.getCubeMapId(), size, false,
            (Shader mappingShader, int framebuffer, int depthRenderBuffer, int mapTarget, int textureToMap, Matrix4f[] mappingViews, int cubeFaceSize) ->
            {
                glBindTextureUnit(0, textureToMap);
                for (int i = 0; i < 6; ++i)
                {
                    mappingShader.setMatrix4f("mappingView", mappingViews[i]);
                    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, mapTarget, 0);
                    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                    DefaultModels.CUBE.bind();
                    DefaultModels.CUBE.drawElements();
                }
            }), size, 0, name);
            generatedCubeMaps.put(name, cubeMap);
            Logger.info(String.format("Generated diffuse irradiance map DIF_IRR_%d | Size: %d", cubeMap.getCubeMapId(), size));
            return cubeMap;
        }
    }

    public static @NotNull GeneratedCubeMap createPrefilteredEnvironmentMap(@NotNull GeneratedCubeMap environmentMap, int size)
    {
        String name = "PREF_" + environmentMap.getCubeMapId();
        final int highestMipLevel = 4;
        if (generatedCubeMaps.containsKey(name))
        {
            return generatedCubeMaps.get(name);
        }
        else
        {
            GeneratedCubeMap cubeMap = new GeneratedCubeMap(mapToCubeMap(PREFILTER_CONVOLUTION, environmentMap.getCubeMapId(), size, true,
            (Shader mappingShader, int framebuffer, int depthRenderBuffer, int mapTarget, int textureToMap, Matrix4f[] mappingViews, int cubeFaceSize) ->
            {
                glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
                glBindTextureUnit(0, textureToMap);
                for (int mip = 0; mip <= highestMipLevel; ++mip)
                {
                    int mipSize = (int) (cubeFaceSize * Math.pow(0.5f, mip));
                    glBindRenderbuffer(GL_RENDERBUFFER, depthRenderBuffer);
                    glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, mipSize, mipSize);
                    glViewport(0, 0, mipSize, mipSize);

                    float roughness = (float) mip / (highestMipLevel + 1);
                    mappingShader.setFloat("u_Roughness", roughness);
                    for (int i = 0; i < 6; ++i)
                    {
                        mappingShader.setMatrix4f("mappingView", mappingViews[i]);
                        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, mapTarget, mip);
                        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                        DefaultModels.CUBE.bind();
                        DefaultModels.CUBE.drawElements();
                    }
                }
            }), size, highestMipLevel, name);
            generatedCubeMaps.put(name, cubeMap);
            Logger.info(String.format("Generated prefiltered environment map PREF_%d | Size: %d", cubeMap.getCubeMapId(), size));
            return cubeMap;
        }
    }

    private static int mapToCubeMap(Shader mappingShader, int textureToMap, int cubeFaceSize, boolean mipmaps, MappingFunction mappingFunction)
    {
        //Create framebuffer for mapping
        int mappingFrameBuffer = glGenFramebuffers(), depthRenderBuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthRenderBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, cubeFaceSize, cubeFaceSize); //Assuming 2:1 equirectangular
        glBindFramebuffer(GL_FRAMEBUFFER, mappingFrameBuffer);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthRenderBuffer);
        if (EntryPoint.DEBUG)
        {
            if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) Logger.error("Cube map mapping framebuffer INCOMPLETE!");
        }

        //Allocating memory for cubemap
        int mapTarget = glCreateTextures(GL_TEXTURE_CUBE_MAP);
        glBindTexture(GL_TEXTURE_CUBE_MAP, mapTarget);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, mipmaps ? GL_LINEAR_MIPMAP_LINEAR : GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        for (int i = 0; i < 6; ++i)
        {
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, cubeFaceSize, cubeFaceSize, 0, GL_RGB, GL_FLOAT, 0);
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
        mappingShader.bind();
        mappingShader.setMatrix4f("model", Matrix4f.identity().scale(new Vector3f(0.5f))); //(Cube .obj is two units in size)
        mappingShader.setMatrix4f("mappingProjection", mappingProjection);

        glViewport(0, 0, cubeFaceSize, cubeFaceSize);
        glClearColor(1f, 1f, 1f, 1f);
        glCullFace(GL_FRONT);

        glBindTexture(GL_TEXTURE_CUBE_MAP, mapTarget);
        mappingFunction.map(mappingShader, mappingFrameBuffer, depthRenderBuffer, mapTarget, textureToMap, mappingViews, cubeFaceSize);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glDeleteFramebuffers(mappingFrameBuffer);
        glDeleteRenderbuffers(depthRenderBuffer);
        glCullFace(GL_BACK);
        return mapTarget;
    }

    public static void exportCubeMap(GeneratedCubeMap cubeMap, String path)
    {
        //Load cubemap faces
        int faceSize = cubeMap.getSize();
        int totalHeight = 0;
        for (int mipLevel = 0; mipLevel <= cubeMap.getHighestMipLevel(); ++mipLevel)
        {
            totalHeight += cubeMap.getSize() * Math.pow(0.5, mipLevel);
        }

        FloatBuffer[][] faceData = new FloatBuffer[cubeMap.getHighestMipLevel() + 1][6]; //Holds raw pixel data from OpenGL

        glBindTexture(GL_TEXTURE_CUBE_MAP, cubeMap.getCubeMapId());
        for (int mipLevel = 0; mipLevel <= cubeMap.getHighestMipLevel(); ++mipLevel)
        {
            for (int i = 0; i < faceData[mipLevel].length; ++i)
            {
                FloatBuffer face = MemoryUtil.memAllocFloat(cubeMap.getSize() * cubeMap.getSize() * 3 * 2);
                glGetTexImage(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, mipLevel, GL_RGBA, GL_FLOAT, face); //OpenGL stores in RGBA even without alpha.
                faceData[mipLevel][i] = face;
            }
        }

        float[][] channelData = new float[3][6 * faceSize * totalHeight]; //Holds raw pixel data for each color channel
        Arrays.fill(channelData[0], 1f);
        Arrays.fill(channelData[1], 0f);
        Arrays.fill(channelData[2], 1f);

        int mipLevelPixelOffset = 0;
        for (int mipLevel = 0; mipLevel < faceData.length; ++mipLevel) // Go through every mipmap
        {
            int mipLevelSize = (int) (faceSize * Math.pow(0.5f, mipLevel));
            int lineOffset = mipLevelSize * 4;
            int mipLineOffset = 6 * faceSize - 6 * mipLevelSize;

            for (int i = 0, pos = mipLevelPixelOffset; i < 6 * mipLevelSize * mipLevelSize; ++i) //Separate color channels from all faces
            {
                int face = i / mipLevelSize % 6;
                int index = i % mipLevelSize;
                int line = i / (mipLevelSize * 6);
                channelData[0][pos] = faceData[mipLevel][face].get(4 * index + line * lineOffset);     //B
                channelData[1][pos] = faceData[mipLevel][face].get(4 * index + line * lineOffset + 1); //G
                channelData[2][pos] = faceData[mipLevel][face].get(4 * index + line * lineOffset + 2); //R
                if (mipLevel > 0 && ((i % (6 * mipLevelSize)) + 1) / (6 * mipLevelSize) == 1) pos += mipLineOffset + 1;
                else ++pos;
            }
            mipLevelPixelOffset += 6 * faceSize * mipLevelSize;
        }

        //The internal storage format is BGRA, this reorders it for TinyEXR.
        FloatBuffer red = MemoryUtil.memAllocFloat(6 * faceSize * totalHeight);
        red.put(channelData[2]);
        red.flip();
        FloatBuffer green = MemoryUtil.memAllocFloat(6 * faceSize * totalHeight);
        green.put(channelData[1]);
        green.flip();
        FloatBuffer blue = MemoryUtil.memAllocFloat(6 * faceSize * totalHeight);
        blue.put(channelData[0]);
        blue.flip();

        //Create EXR image
        EXRHeader exr_header = EXRHeader.create();
        TinyEXR.InitEXRHeader(exr_header);

        EXRImage exr_image = EXRImage.create();
        TinyEXR.InitEXRImage(exr_image);

        int numChannels = 3;
        exr_image.num_channels(numChannels);
        exr_header.num_channels(numChannels);

        PointerBuffer imagesPtr = BufferUtils.createPointerBuffer(numChannels);
        imagesPtr.put(red);
        imagesPtr.put(green);
        imagesPtr.put(blue);
        imagesPtr.flip();

        exr_image.images(imagesPtr);
        exr_image.width(6 * faceSize);
        exr_image.height(totalHeight);

        EXRChannelInfo.Buffer channelInfos = EXRChannelInfo.create(numChannels);
        exr_header.channels(channelInfos);
        channelInfos.get(0).name(IOUtils.stringToByteBuffer("R\0"));
        channelInfos.get(1).name(IOUtils.stringToByteBuffer("G\0"));
        channelInfos.get(2).name(IOUtils.stringToByteBuffer("B\0"));

        IntBuffer pixelTypes = BufferUtils.createIntBuffer(exr_header.num_channels());
        IntBuffer requestedPixelTypes = BufferUtils.createIntBuffer(exr_header.num_channels());
        exr_header.pixel_types(pixelTypes);
        exr_header.requested_pixel_types(requestedPixelTypes);
        for (int i = 0; i < exr_header.num_channels(); i++)
        {
            pixelTypes.put(i, TinyEXR.TINYEXR_PIXELTYPE_FLOAT);
            requestedPixelTypes.put(i, TinyEXR.TINYEXR_PIXELTYPE_FLOAT);
        }

        PointerBuffer err = BufferUtils.createPointerBuffer(1);
        int ret = TinyEXR.SaveEXRImageToFile(exr_image, exr_header, path, err);
        if (ret != TinyEXR.TINYEXR_SUCCESS)
        {
            Logger.error("Could not export EXR + " + path + ": " + err.get(0));
        } else
        {
            Logger.info("Exported EXR to " + path);
        }

        for (FloatBuffer[] facesAtMipLevel : faceData)
        {
            for (FloatBuffer buffer : facesAtMipLevel) MemoryUtil.memFree(buffer);
        }
        MemoryUtil.memFree(red);
        MemoryUtil.memFree(green);
        MemoryUtil.memFree(blue);
    }

    public static void dispose()
    {
        generatedCubeMaps.forEach((key, value) -> value.delete());
    }

    @FunctionalInterface
    private interface MappingFunction
    {
        void map(Shader mappingShader, int framebuffer, int depthRenderBuffer, int mapTarget, int textureToMap, Matrix4f[] mappingViews, int cubeFaceSize);
    }
}
