package engine.graphics;

public abstract class Material
{
    protected Shader shader;

    public Material(Shader shader)
    {
        this.shader = shader;
    }

    public abstract void bind();

    public abstract void unbind();

    public Shader getShader()
    {
        return shader;
    }
}