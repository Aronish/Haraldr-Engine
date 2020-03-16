package engine.graphics;

import engine.math.Vector3f;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DiffuseMaterial extends Material
{
    public static final Material DEFAULT = new DiffuseMaterial();

    private Vector3f ambient;
    private Vector3f diffuse;
    private Vector3f specular;
    private float specularExponent;
    private float opacity;
    private Texture diffuseTexture;

    public DiffuseMaterial()
    {
        this(new Vector3f(1f), new Vector3f(1f), new Vector3f(1f), 4f, 1f, Texture.DEFAULT_TEXTURE);
    }

    public DiffuseMaterial(Texture diffuseTexture)
    {
        this(new Vector3f(1f), new Vector3f(1f), new Vector3f(1f), 4f, 1f, diffuseTexture);
    }

    public DiffuseMaterial(Vector3f ambient, Vector3f diffuse, Vector3f specular, float specularExponent, float opacity)
    {
        this(ambient, diffuse, specular, specularExponent, opacity, Texture.DEFAULT_TEXTURE);
    }

    public DiffuseMaterial(Vector3f ambient, Vector3f diffuse, Vector3f specular, float specularExponent, float opacity, Texture diffuseTexture)
    {
        super(Shader.DIFFUSE);
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.specularExponent = specularExponent;
        this.opacity = opacity;
        this.diffuseTexture = diffuseTexture;
    }

    @Override
    public void bind()
    {
        shader.bind();
        shader.setVector3f(ambient, "material.ambientColor");
        shader.setVector3f(diffuse, "material.diffuseColor");
        shader.setVector3f(specular, "material.specularColor");
        shader.setFloat(specularExponent, "material.specularExponent");
        shader.setFloat(opacity, "material.opacity");
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

    public void setSpecularExponent(float specularExponent)
    {
        this.specularExponent = specularExponent;
    }

    public void setOpacity(float opacity)
    {
        this.opacity = opacity;
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

    public float getSpecularExponent()
    {
        return specularExponent;
    }

    public float getOpacity()
    {
        return opacity;
    }
}
