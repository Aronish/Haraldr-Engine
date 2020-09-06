package offlinerenderer;

import haraldr.debug.Logger;
import haraldr.graphics.DefaultModels;
import haraldr.graphics.Shader;
import haraldr.main.EntryPoint;
import haraldr.main.IOUtils;
import haraldr.math.Matrix4f;
import haraldr.math.Vector3f;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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
                ByteBuffer data = IOUtils.readResource(path, (stream) -> IOUtils.resourceToByteBuffer(stream, 4 * 4096)); //Assuming hdr format as 4 8bit channels
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
            }), size, path);
            glDeleteTextures(texture);
            glGenerateMipmap(GL_TEXTURE_CUBE_MAP); // Do this after having set the textures. Used by other pbr maps.

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
            }), size, name);
            generatedCubeMaps.put(name, cubeMap);
            Logger.info(String.format("Generated diffuse irradiance map DIF_IRR_%d | Size: %d", cubeMap.getCubeMapId(), size));
            return cubeMap;
        }
    }

    public static @NotNull GeneratedCubeMap createPrefilteredEnvironmentMap(@NotNull GeneratedCubeMap environmentMap, int size)
    {
        String name = "PREF_" + environmentMap.getCubeMapId();
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
                int maxMipLevels = 5;
                for (int mip = 0; mip < maxMipLevels; ++mip)
                {
                    int mipSize = (int) (cubeFaceSize * Math.pow(0.5f, mip));
                    glBindRenderbuffer(GL_RENDERBUFFER, depthRenderBuffer);
                    glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, mipSize, mipSize);
                    glViewport(0, 0, mipSize, mipSize);

                    float roughness = (float) mip / (maxMipLevels - 1);
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
            }), size, name);
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
