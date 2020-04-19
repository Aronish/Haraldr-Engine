package engine.graphics.material;

import engine.graphics.ResourceManager;
import engine.graphics.Shader;
import engine.graphics.Texture;

import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess"})
public class NormalMaterial extends Material
{
    private Texture diffuseTexture, normalMap;

    public NormalMaterial(String diffuseTexture, String normalMap)
    {
        this(ResourceManager.getTexture(diffuseTexture, true), ResourceManager.getTexture(normalMap, false));
    }

    public NormalMaterial(Texture diffuseTexture, Texture normalMap)
    {
        super(Shader.NORMAL);
        this.diffuseTexture = diffuseTexture;
        this.normalMap = normalMap;
    }

    public NormalMaterial(String diffuseTexture, String normalMap, float diffuseStrength, float specularStrength, float specularExponent, float opacity)
    {
        this(ResourceManager.getTexture(diffuseTexture, true), ResourceManager.getTexture(normalMap, false), diffuseStrength, specularStrength, specularExponent, opacity);
    }

    public NormalMaterial(Texture diffuseTexture, Texture normalMap, float diffuseStrength, float specularStrength, float specularExponent, float opacity)
    {
        super(Shader.NORMAL, diffuseStrength, specularStrength, specularExponent, opacity);
        this.diffuseTexture = diffuseTexture;
        this.normalMap = normalMap;
    }

    @Override
    public void bind()
    {
        super.bind();
        diffuseTexture.bind(0);
        normalMap.bind(1);
    }

    @Override
    public void unbind()
    {
        shader.unbind();
        diffuseTexture.unbind(0);
        normalMap.unbind(1);
    }

    public void delete()
    {
        unbind();
        diffuseTexture.delete();
        normalMap.delete();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NormalMaterial that = (NormalMaterial) o;
        return diffuseTexture.equals(that.diffuseTexture) &&
                normalMap.equals(that.normalMap);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(diffuseTexture, normalMap);
    }
}
