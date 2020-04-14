package engine.graphics;

import engine.math.Vector3f;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DiffuseMaterial extends Material
{
    public static final Material DEFAULT = new DiffuseMaterial();

    private Vector3f ambient;
    private Vector3f diffuse;
    private Vector3f specular;
    private Texture diffuseTexture;

    public DiffuseMaterial()
    {
        this(new Vector3f(1f), new Vector3f(1f), new Vector3f(1f), Texture.DEFAULT_TEXTURE);
    }

    public DiffuseMaterial(String diffuseTexture)
    {
        this(new Vector3f(1f), new Vector3f(1f), new Vector3f(1f), diffuseTexture);
    }

    public DiffuseMaterial(Texture diffuseTexture)
    {
        this(new Vector3f(1f), new Vector3f(1f), new Vector3f(1f), diffuseTexture);
    }

    public DiffuseMaterial(Vector3f ambient, Vector3f diffuse, Vector3f specular)
    {
        this(ambient, diffuse, specular, Texture.DEFAULT_TEXTURE);
    }

    public DiffuseMaterial(Vector3f ambient, Vector3f diffuse, Vector3f specular, String diffuseTexture)
    {
        this(ambient, diffuse, specular, ResourceManager.getTexture(diffuseTexture));
    }

    public DiffuseMaterial(Vector3f ambient, Vector3f diffuse, Vector3f specular, Texture diffuseTexture)
    {
        super(Shader.DIFFUSE, 0.5f);
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.diffuseTexture = diffuseTexture;
    }

    public DiffuseMaterial(Vector3f ambient, Vector3f diffuse, Vector3f specular, float diffuseStrength)
    {
        this(ambient, diffuse, specular, diffuseStrength, Texture.DEFAULT_TEXTURE);
    }

    public DiffuseMaterial(Vector3f ambient, Vector3f diffuse, Vector3f specular, float diffuseStrength, String diffuseTexture)
    {
        this(ambient, diffuse, specular, diffuseStrength, ResourceManager.getTexture(diffuseTexture));
    }

    public DiffuseMaterial(Vector3f ambient, Vector3f diffuse, Vector3f specular, float diffuseStrength, Texture diffuseTexture)
    {
        super(Shader.DIFFUSE, diffuseStrength);
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.diffuseTexture = diffuseTexture;
    }

    public DiffuseMaterial(Vector3f ambient, Vector3f diffuse, Vector3f specular, float diffuseStrength, float specularStrength, float specularExponent, float opacity)
    {
        this(ambient, diffuse, specular, diffuseStrength, specularStrength, specularExponent, opacity, Texture.DEFAULT_TEXTURE);
    }

    public DiffuseMaterial(Vector3f ambient, Vector3f diffuse, Vector3f specular, float diffuseStrength, float specularStrength, float specularExponent, float opacity, String diffuseTexture)
    {
        this(ambient, diffuse, specular, diffuseStrength, specularStrength, specularExponent, opacity, ResourceManager.getTexture(diffuseTexture));
    }

    public DiffuseMaterial(Vector3f ambient, Vector3f diffuse, Vector3f specular, float diffuseStrength, float specularStrength, float specularExponent, float opacity, Texture diffuseTexture)
    {
        super(Shader.DIFFUSE, diffuseStrength, specularStrength, specularExponent, opacity);
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.diffuseTexture = diffuseTexture;
    }

    @Override
    public void bind()
    {
        super.bind();
        shader.setVector3f(ambient, "materialProperties.ambientColor");
        shader.setVector3f(diffuse, "materialProperties.diffuseColor");
        shader.setVector3f(specular, "materialProperties.specularColor");
        diffuseTexture.bind(0);
    }

    @Override
    public void unbind()
    {
        shader.unbind();
    }

    public void setAmbient(Vector3f ambient)
    {
        this.ambient = ambient;
    }

    public void setDiffuse(Vector3f diffuse)
    {
        this.diffuse = diffuse;
    }

    public void setSpecular(Vector3f specular)
    {
        this.specular = specular;
    }

    public Vector3f getAmbient()
    {
        return ambient;
    }

    public Vector3f getDiffuse()
    {
        return diffuse;
    }

    public Vector3f getSpecular()
    {
        return specular;
    }
}
