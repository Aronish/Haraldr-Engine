package engine.graphics;

import java.util.HashMap;
import java.util.Map;

public class ResourceManager
{
    private static final Map<String, Texture> TEXTURES      = new HashMap<>();
    private static final Map<String, VertexArray> MESHES    = new HashMap<>();
    private static final Map<String, Shader> SHADERS        = new HashMap<>();
    private static final Map<String, CubeMap> CUBEMAPS      = new HashMap<>();

    public static Texture getTexture(String path, boolean isColorData)
    {
        if (TEXTURES.containsKey(path))
        {
            return TEXTURES.get(path);
        }
        else
        {
            return Texture.create(path, isColorData);
        }
    }

    public static VertexArray getMesh(String path)
    {
        if (MESHES.containsKey(path))
        {
            return MESHES.get(path);
        }
        else
        {
            return ObjParser.loadMesh(path);
        }
    }

    public static Shader getShader(String path)
    {
        if (SHADERS.containsKey(path))
        {
            return SHADERS.get(path);
        }
        else
        {
            return Shader.create(path);
        }
    }

    public static void addTexture(String path, Texture texture)
    {
        TEXTURES.put(path, texture);
    }

    public static Texture getLoadedTexture(String path)
    {
        return TEXTURES.get(path);
    }

    public static boolean isTextureLoaded(String path)
    {
        return TEXTURES.containsKey(path);
    }

    public static void addMesh(String path, VertexArray mesh)
    {
        MESHES.put(path, mesh);
    }

    public static VertexArray getLoadedMesh(String path)
    {
        return MESHES.get(path);
    }

    public static boolean isMeshLoaded(String path)
    {
        return MESHES.containsKey(path);
    }

    public static void addShader(String path, Shader shader)
    {
        SHADERS.put(path, shader);
    }

    public static Shader getLoadedShader(String path)
    {
        return SHADERS.get(path);
    }

    public static boolean isShaderLoaded(String path)
    {
        return SHADERS.containsKey(path);
    }

    public static void addCubeMap(String path, CubeMap cubeMap)
    {
        CUBEMAPS.put(path, cubeMap);
    }

    public static CubeMap getLoadedCubeMap(String path)
    {
        return CUBEMAPS.get(path);
    }

    public static boolean isCubeMapLoaded(String path)
    {
        return CUBEMAPS.containsKey(path);
    }

    public static void dispose()
    {
        TEXTURES.forEach((key, texture) -> texture.delete());
        MESHES.forEach((key, mesh) -> mesh.delete());
        SHADERS.forEach((key, shader) -> shader.delete());
        CUBEMAPS.forEach((key, cubeMap) -> cubeMap.delete());
    }
}
