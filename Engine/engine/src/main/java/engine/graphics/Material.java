package engine.graphics;

public class Material
{
    private Texture diffuseTexture, normalMap;
    private Shader shader;

    public Material(String diffuseTexture, String normalMap, Shader shader)
    {
        this.diffuseTexture = new Texture(diffuseTexture);
        this.normalMap = new Texture(normalMap);
        this.shader = shader;
    }

    public void bind()
    {
        shader.bind();
        diffuseTexture.bind(0);
        normalMap.bind(1);
    }

    public void unbind()
    {
        shader.bind();
        diffuseTexture.unbind(0);
        normalMap.unbind(1);
    }

    public Shader getShader()
    {
        return shader;
    }
}
