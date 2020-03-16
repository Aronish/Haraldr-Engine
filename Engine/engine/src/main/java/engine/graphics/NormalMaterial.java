package engine.graphics;

@SuppressWarnings({"unused", "WeakerAccess"})
public class NormalMaterial extends Material
{
    private Texture diffuseTexture, normalMap;

    public NormalMaterial(String diffuseTexture, String normalMap)
    {
        this(new Texture(diffuseTexture), new Texture(normalMap));
    }

    public NormalMaterial(Texture diffuseTexture, Texture normalMap)
    {
        super(Shader.NORMAL);
        this.diffuseTexture = diffuseTexture;
        this.normalMap = normalMap;
    }

    @Override
    public void bind()
    {
        shader.bind();
        diffuseTexture.bind(0);
        normalMap.bind(1);
    }

    @Override
    public void unbind()
    {
        shader.bind();
        diffuseTexture.unbind(0);
        normalMap.unbind(1);
    }
}
