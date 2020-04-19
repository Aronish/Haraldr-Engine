package engine.graphics;

import java.util.HashMap;
import java.util.Map;

public class ResourceManager
{
    private static final Map<String, Texture> TEXTURES = new HashMap<>();
    private static final Map<String, VertexArray> MESHES = new HashMap<>();

    public static Texture getTexture(String path, boolean isColorData)
    {
        if (TEXTURES.containsKey(path))
        {
            return TEXTURES.get(path);
        }
        else
        {
            Texture texture = new Texture(path, isColorData);
            TEXTURES.put(path, texture);
            return texture;
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
            VertexArray mesh = ObjParser.loadMesh(path);
            MESHES.put(path, mesh);
            return mesh;
        }
    }
}
