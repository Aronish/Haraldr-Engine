package engine.graphics.material;

import engine.graphics.Shader;

public abstract class Material
{
    protected Shader shader;
    private float diffuseStrength, specularStrength, specularExponent, opacity; // Actually maybe temporary

    public Material(Shader shader)
    {
        this(shader, 1f, 0.5f, 32f, 1f);
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

    public void unbind()
    {
        shader.unbind();
    }

    public Shader getShader()
    {
        return shader;
    }
}