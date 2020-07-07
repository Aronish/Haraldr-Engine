package engine.graphics.material;

import engine.graphics.Shader;

@Deprecated
public abstract class Material
{
    protected Shader shader;

    public Material()
    {
    }

    public Material(Shader shader)
    {
        this.shader = shader;
    }

    public void bind()
    {
        shader.bind();
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