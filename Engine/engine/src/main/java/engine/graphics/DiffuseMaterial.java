package engine.graphics;

import engine.math.Vector3f;

@SuppressWarnings("unused")
public class DiffuseMaterial extends Material
{
    private Vector3f ambient;
    private Vector3f diffuse;
    private Vector3f specular;
    private float specularExponent;
    private float opacity;

    public DiffuseMaterial()
    {
        this(new Vector3f(1f), new Vector3f(1f), new Vector3f(1f), 4f, 1f);
    }

    public DiffuseMaterial(Vector3f ambient, Vector3f diffuse, Vector3f specular, float specularExponent, float opacity)
    {
        super(Shader.DIFFUSE);
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.specularExponent = specularExponent;
        this.opacity = opacity;
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
