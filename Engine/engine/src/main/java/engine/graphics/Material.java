package engine.graphics;

public abstract class Material
{
    protected Shader shader;
    protected float diffuseStrength, specularStrength, specularExponent, opacity; //Maybe temporary

    public Material(Shader shader)
    {
        this(shader, 1f, 1f, 128f, 1f);
    }

    public Material(Shader shader, float diffuseStrength)
    {
        this(shader, diffuseStrength, 1f, 128f, 1f);
    }

    public Material(Shader shader, float diffuseStrength, float specularStrength, float specularExponent, float opacity)
    {
        this.shader = shader;
        this.diffuseStrength = diffuseStrength;
        this.specularStrength = specularStrength;
        this.specularExponent = specularExponent;
        this.opacity = opacity;
    }

    public void bind()
    {
        shader.bind();
        shader.setFloat(diffuseStrength, "materialProperties.diffuseStrength");
        shader.setFloat(specularStrength, "materialProperties.specularStrength");
        shader.setFloat(specularExponent, "materialProperties.specularExponent");
        shader.setFloat(opacity, "materialProperties.opacity");
    }

    public abstract void unbind();

    public Shader getShader()
    {
        return shader;
    }
}