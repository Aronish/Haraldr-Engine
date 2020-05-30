package engine.graphics.material;

import engine.graphics.CubeMap;
import engine.graphics.ResourceManager;
import engine.graphics.Shader;
import engine.graphics.Texture;

import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess"})
public class RefractiveMaterial extends Material
{
    private CubeMap environmentMap;
    private float refractiveRatio; // Common indices: Air: 1, Water: 1.33, Ice: 1.309, Glass: 1.52, Diamond: 2.42 | Ratio = from / to
    private Texture diffuseTexture, refractionMap;

    public RefractiveMaterial(CubeMap environmentMap)
    {
        this(1f / 1.52f, environmentMap); // Default: Air to Glass
    }

    public RefractiveMaterial(float refractiveRatio, CubeMap environmentMap)
    {
        this(Texture.DEFAULT_WHITE, Texture.DEFAULT_WHITE, refractiveRatio, environmentMap);
    }

    public RefractiveMaterial(String diffuseTexture, String refractionMap, CubeMap environmentMap)
    {
        this(ResourceManager.getTexture(diffuseTexture, true), ResourceManager.getTexture(refractionMap, false), 1f / 1.52f, environmentMap);
    }

    public RefractiveMaterial(Texture diffuseTexture, Texture refractionMap, CubeMap environmentMap)
    {
        this(diffuseTexture, refractionMap, 1f / 1.52f, environmentMap);
    }

    public RefractiveMaterial(String diffuseTexture, String refractionMap, CubeMap environmentMap, float refractiveRatio)
    {
        this(ResourceManager.getTexture(diffuseTexture, true), ResourceManager.getTexture(refractionMap, false), refractiveRatio, environmentMap);
    }

    public RefractiveMaterial(Texture diffuseTexture, Texture refractionMap, float refractiveRatio, CubeMap environmentMap)
    {
        super(Shader.REFRACTIVE);
        this.environmentMap = environmentMap;
        this.refractiveRatio = refractiveRatio;
        this.diffuseTexture = diffuseTexture;
        this.refractionMap = refractionMap;
    }

    public void setRefractiveRatio(float refractiveRatio)
    {
        this.refractiveRatio = refractiveRatio;
    }

    @Override
    public void bind()
    {
        shader.bind();
        shader.setFloat("refractiveRatio", refractiveRatio);
        environmentMap.bind(0);
        diffuseTexture.bind(1);
        refractionMap.bind(2);
    }

    @Override
    public void unbind()
    {
        shader.unbind();
        environmentMap.unbind(0);
        diffuseTexture.unbind(1);
        refractionMap.unbind(2);
    }
}
