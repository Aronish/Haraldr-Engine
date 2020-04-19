package engine.graphics.material;

import engine.graphics.CubeMap;
import engine.graphics.ResourceManager;
import engine.graphics.Shader;
import engine.graphics.Texture;

import java.util.Objects;

public class ReflectiveMaterial extends Material
{
    private CubeMap environmentMap;
    private Texture diffuseTexture, reflectionMap;

    public ReflectiveMaterial(CubeMap environmentMap)
    {
        this(environmentMap, Texture.DEFAULT_TEXTURE, Texture.DEFAULT_TEXTURE);
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReflectiveMaterial that = (ReflectiveMaterial) o;
        return environmentMap.equals(that.environmentMap) &&
                diffuseTexture.equals(that.diffuseTexture) &&
                reflectionMap.equals(that.reflectionMap);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(environmentMap, diffuseTexture, reflectionMap);
    }
}
