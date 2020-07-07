package engine.graphics.material;

import engine.graphics.ResourceManager;
import engine.graphics.Shader;
import engine.graphics.Texture;

@Deprecated
@SuppressWarnings({"unused", "WeakerAccess"})
public class NormalMaterial extends Material
{
    private Texture diffuseTexture, normalMap;
    private float specularStrength = 0.5f, specularExponent = 32f, opacity = 1f;

    public NormalMaterial(String diffuseTexture, String normalMap)
    {
        this(Texture.create(diffuseTexture, true), Texture.create(normalMap, false));
    }

    public NormalMaterial(Texture diffuseTexture, Texture normalMap)
    {
        //super(Shader.NORMAL);
        this.diffuseTexture = diffuseTexture;
        this.normalMap = normalMap;
    }

    public NormalMaterial(String diffuseTexture, String normalMap, float specularStrength, float specularExponent, float opacity)
    {
        this(Texture.create(diffuseTexture, true), Texture.create(normalMap, false), specularStrength, specularExponent, opacity);
    }

    public NormalMaterial(Texture diffuseTexture, Texture normalMap, float specularStrength, float specularExponent, float opacity)
    {
        //super(Shader.NORMAL);
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
        shader.setFloat("u_MaterialProperties.specularStrength", specularStrength);
        shader.setFloat("u_MaterialProperties.specularExponent", specularExponent);
        shader.setFloat("u_MaterialProperties.opacity", opacity);
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
