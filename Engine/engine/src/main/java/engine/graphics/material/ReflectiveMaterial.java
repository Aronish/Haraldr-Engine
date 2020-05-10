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
        this(environmentMap, Texture.DEFAULT_WHITE, Texture.DEFAULT_WHITE);
    }

    public ReflectiveMaterial(CubeMap environmentMap, String diffuseTexture, String reflectionMap)
    {
        this(environmentMap, ResourceManager.getTexture(diffuseTexture, true), ResourceManager.getTexture(reflectionMap, false));
    }

    public ReflectiveMaterial(CubeMap environmentMap, Texture diffuseTexture, Texture reflectionMap)
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
