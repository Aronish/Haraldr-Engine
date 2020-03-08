package engine.graphics;

public class Material
{
    private Texture diffuseColor;
    private Texture normalMap;
    private Shader shader;

    public Material(String diffuseColor, String normalMap, Shader shader)
    {
        this.diffuseColor = new Texture(diffuseColor);
        this.normalMap = new Texture(normalMap);
        this.shader = shader;
    }

    public void bind()
    {
        shader.bind();
        diffuseColor.bind(0);
        normalMap.bind(1);
    }

    public void unbind()
    {
        shader.bind();
        diffuseColor.unbind(0);
        normalMap.unbind(1);
    }

    public Shader getShader()
    {
        return shader;
    }
}
