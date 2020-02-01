package engine.graphics;

import engine.math.Vector3f;

public class Material
{
    private Vector3f ambient, diffuse, specular;
    private float specularExponent, opacity;

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
