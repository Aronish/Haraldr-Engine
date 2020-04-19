package engine.graphics.material;

import engine.graphics.ResourceManager;
import engine.graphics.Shader;
import engine.graphics.Texture;
import engine.math.Vector3f;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DiffuseMaterial extends Material
{
    public static final Material DEFAULT = new DiffuseMaterial();

    private Vector3f diffuseColor;
    private Texture diffuseTexture;

    /////FLAT COLOR/////////

    public DiffuseMaterial()
    {
        this(new Vector3f(1f));
    }

    public DiffuseMaterial(Vector3f diffuseColor)
    {
        super(Shader.DIFFUSE);
        this.diffuseColor = diffuseColor;
        this.diffuseTexture = Texture.DEFAULT_TEXTURE;
    }

    public DiffuseMaterial(Vector3f diffuseColor, float diffuseStrength, float specularStrength, float specularExponent, float opacity)
    {
        super(Shader.DIFFUSE, diffuseStrength, specularStrength, specularExponent, opacity);
        this.diffuseColor = diffuseColor;
        this.diffuseTexture = Texture.DEFAULT_TEXTURE;
    }

    /////TEXTURED////////////////////////////////

    public DiffuseMaterial(String diffuseTexture)
    {
        this(ResourceManager.getTexture(diffuseTexture, true));
    }

    public DiffuseMaterial(Texture diffuseTexture)
    {
        super(Shader.DIFFUSE);
        this.diffuseColor = new Vector3f(1f);
        this.diffuseTexture = diffuseTexture;
    }

    public DiffuseMaterial(String diffuseTexture, float diffuseStrength, float specularStrength, float specularExponent, float opacity)
    {
        this(ResourceManager.getTexture(diffuseTexture, true), diffuseStrength, specularStrength, specularExponent, opacity);
    }

    public DiffuseMaterial(Texture diffuseTexture, float diffuseStrength, float specularStrength, float specularExponent, float opacity)
    {
        super(Shader.DIFFUSE, diffuseStrength, specularStrength, specularExponent, opacity);
        this.diffuseColor = new Vector3f(1f);
        this.diffuseTexture = diffuseTexture;
    }

    @Override
    public void bind()
    {
        super.bind();
        shader.setVector3f(diffuseColor, "materialProperties.diffuseColor");
        diffuseTexture.bind(0);
    }

    @Override
    public void unbind()
    {
        shader.unbind();
        diffuseTexture.unbind(0);
    }
}
