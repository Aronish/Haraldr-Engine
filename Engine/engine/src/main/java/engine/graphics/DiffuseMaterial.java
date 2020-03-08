package engine.graphics;

import engine.math.Vector3f;

public class DiffuseMaterial
{
    private Vector3f ambient = new Vector3f(1f), diffuse = new Vector3f(1f), specular = new Vector3f(1f);
    private float specularExponent = 4f, opacity = 1f;

    public DiffuseMaterial() {}

    public DiffuseMaterial(Vector3f ambient, Vector3f diffuse, Vector3f specular, float specularExponent, float opacity)
    {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.specularExponent = specularExponent;
        this.opacity = opacity;
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
