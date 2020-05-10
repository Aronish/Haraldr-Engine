package engine.graphics.material;

import engine.graphics.ResourceManager;
import engine.graphics.Shader;
import engine.graphics.Texture;

@SuppressWarnings({"unused", "WeakerAccess"})
public class NormalMaterial extends Material
{
    private Texture diffuseTexture, normalMap;
    private float specularStrength = 0.5f, specularExponent = 32f, opacity = 1f;

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
        super(Shader.NORMAL);
        this.diffuseTexture = diffuseTexture;
        this.normalMap = normalMap;
        this.specularStrength = specularStrength;
        this.specularExponent = specularExponent;
        this.opacity = opacity;
    }

    @Override
    public void bind()
    {
        super.bind();
        shader.setFloat(specularStrength, "materialProperties.specularStrength");
        shader.setFloat(specularExponent, "materialProperties.specularExponent");
        shader.setFloat(opacity, "materialProperties.opacity");
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
}
