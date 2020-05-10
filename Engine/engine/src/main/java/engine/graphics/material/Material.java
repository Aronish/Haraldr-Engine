package engine.graphics.material;

import engine.graphics.Shader;

public abstract class Material
{
    protected Shader shader;

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