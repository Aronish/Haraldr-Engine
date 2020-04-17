package engine.graphics.material;

import engine.graphics.CubeMap;
import engine.graphics.ResourceManager;
import engine.graphics.Shader;
import engine.graphics.Texture;

public class ReflectiveDiffuseMaterial extends Material
{
    private CubeMap environmentMap;
    private Texture diffuseTexture, reflectionMap;

    public ReflectiveDiffuseMaterial(CubeMap environmentMap, String diffuseTexture, String reflectionMap)
    {
        super(Shader.REFLECTIVE_DIFFUSE);
        this.environmentMap = environmentMap;
        this.diffuseTexture = ResourceManager.getTexture(diffuseTexture);
        this.reflectionMap = ResourceManager.getTexture(reflectionMap);
    }

    @Override
    public void bind()
    {
        shader.bind();
        environmentMap.bind(0);
        diffuseTexture.bind(1);
        reflectionMap.bind(2);
    }

    @Override
    public void unbind()
    {
        shader.unbind();
        environmentMap.unbind(0);
        diffuseTexture.unbind(1);
        reflectionMap.unbind(2);
    }
}
