package engine.graphics.material;

import engine.graphics.ResourceManager;
import engine.graphics.Shader;
import engine.graphics.Texture;
import engine.math.Vector3f;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DiffuseMaterial extends Material
{
    public static final Material DEFAULT = new DiffuseMaterial();

    private Vector3f diffuseColor = new Vector3f(1f);
    private float diffuseStrength = 1f, specularStrength = 0.5f, specularExponent = 32f, opacity = 1f;
    private Texture diffuseTexture;

    /////FLAT COLOR/////////

    public DiffuseMaterial()
    {
        //super(Shader.DIFFUSE);
    }

    public DiffuseMaterial(Shader shader, Vector3f diffuseColor, float diffuseStrength, float specularStrength, float specularExponent, float opacity)
    {
        super(shader);
        this.diffuseColor = diffuseColor;
        this.diffuseTexture = Texture.DEFAULT_WHITE;
        this.diffuseStrength = diffuseStrength;
        this.specularStrength = specularStrength;
        this.specularExponent = specularExponent;
        this.opacity = opacity;
    }

    public DiffuseMaterial(Shader shader, Texture diffuseTexture, float diffuseStrength, float specularStrength, float specularExponent, float opacity)
    {
        super(shader);
        this.diffuseTexture = diffuseTexture;
        this.diffuseStrength = diffuseStrength;
        this.specularStrength = specularStrength;
        this.specularExponent = specularExponent;
        this.opacity = opacity;
    }

    public DiffuseMaterial(Vector3f diffuseColor)
    {
        //super(Shader.DIFFUSE);
        this.diffuseColor = diffuseColor;
    }

    public DiffuseMaterial(Vector3f diffuseColor, float diffuseStrength, float specularStrength, float specularExponent, float opacity)
    {
        //super(Shader.DIFFUSE);
        this.diffuseColor = diffuseColor;
        this.diffuseTexture = Texture.DEFAULT_WHITE;
        this.diffuseStrength = diffuseStrength;
        this.specularStrength = specularStrength;
        this.specularExponent = specularExponent;
        this.opacity = opacity;
    }

    /////TEXTURED////////////////////////////////

    public DiffuseMaterial(String diffuseTexture)
    {
        this(ResourceManager.getTexture(diffuseTexture, true));
    }

    public DiffuseMaterial(Texture diffuseTexture)
    {
        //super(Shader.DIFFUSE);
        this.diffuseTexture = diffuseTexture;
    }

    public DiffuseMaterial(String diffuseTexture, float diffuseStrength, float specularStrength, float specularExponent, float opacity)
    {
        this(ResourceManager.getTexture(diffuseTexture, true), diffuseStrength, specularStrength, specularExponent, opacity);
    }

    public DiffuseMaterial(Texture diffuseTexture, float diffuseStrength, float specularStrength, float specularExponent, float opacity)
    {
        //super(Shader.DIFFUSE);
        this.diffuseTexture = diffuseTexture;
        this.diffuseStrength = diffuseStrength;
        this.specularStrength = specularStrength;
        this.specularExponent = specularExponent;
        this.opacity = opacity;
    }

    @Override
    public void bind()
    {
        super.bind();
        shader.setVector3f("u_MaterialProperties.diffuseColor", diffuseColor);
        shader.setFloat("u_MaterialProperties.diffuseStrength", diffuseStrength);
        shader.setFloat("u_MaterialProperties.specularStrength", specularStrength);
        shader.setFloat("u_MaterialProperties.specularExponent", specularExponent);
        shader.setFloat("u_MaterialProperties.opacity", opacity);
        diffuseTexture.bind(0);
    }

    @Override
    public void unbind()
    {
        shader.unbind();
        diffuseTexture.unbind(0);
    }
}
