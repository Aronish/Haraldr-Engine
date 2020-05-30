package engine.graphics.material;

import engine.graphics.CubeMap;
import engine.graphics.ResourceManager;
import engine.graphics.Shader;
import engine.graphics.Texture;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ReflectiveMaterial extends Material
{
    private CubeMap environmentMap;
    private Texture diffuseTexture, reflectionMap;

    public ReflectiveMaterial(CubeMap environmentMap)
    {
        this(Texture.DEFAULT_WHITE, Texture.DEFAULT_WHITE, environmentMap);
    }

    public ReflectiveMaterial(String diffuseTexture, String reflectionMap, CubeMap environmentMap)
    {
        this(ResourceManager.getTexture(diffuseTexture, true), ResourceManager.getTexture(reflectionMap, false), environmentMap);
    }

    public ReflectiveMaterial(Texture diffuseTexture, Texture reflectionMap, CubeMap environmentMap)
    {
        super(Shader.REFLECTIVE);
        this.environmentMap = environmentMap;
        this.diffuseTexture = diffuseTexture;
        this.reflectionMap = reflectionMap;
    }

    @Override
    public void bind()
    {
        shader.bind();
        environmentMap.bind(0);
        diffuseTexture.bind(1);
        reflectionMap.bind(2);
    }

    @Override
    public void unbind()
    {
        shader.unbind();
        environmentMap.unbind(0);
        diffuseTexture.unbind(1);
        reflectionMap.unbind(2);
    }
}
