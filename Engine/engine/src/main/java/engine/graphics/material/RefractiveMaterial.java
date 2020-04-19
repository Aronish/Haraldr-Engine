package engine.graphics.material;

import engine.graphics.CubeMap;
import engine.graphics.ResourceManager;
import engine.graphics.Shader;
import engine.graphics.Texture;

@SuppressWarnings({"unused", "WeakerAccess"})
public class RefractiveMaterial extends Material
{
    private CubeMap environmentMap;
    private float refractiveRatio; // Common indices: Air: 1, Water: 1.33, Ice: 1.309, Glass: 1.52, Diamond: 2.42 | Ratio = from / to
    private Texture diffuseTexture, refractionMap;

    public RefractiveMaterial(CubeMap environmentMap)
    {
        this(environmentMap, 1f / 1.52f); // Default: Air to Glass
    }

    public RefractiveMaterial(CubeMap environmentMap, float refractiveRatio)
    {
        this(environmentMap, refractiveRatio, Texture.DEFAULT_TEXTURE, Texture.DEFAULT_TEXTURE);
    }

    public RefractiveMaterial(CubeMap environmentMap, String diffuseTexture, String refractionMap)
    {
        this(environmentMap, 1f / 1.52f, ResourceManager.getTexture(diffuseTexture, true), ResourceManager.getTexture(refractionMap, false));
    }

    public RefractiveMaterial(CubeMap environmentMap, Texture diffuseTexture, Texture refractionMap)
    {
        this(environmentMap, 1f / 1.52f, diffuseTexture, refractionMap);
    }

    public RefractiveMaterial(CubeMap environmentMap, float refractiveRatio, String diffuseTexture, String refractionMap)
    {
        this(environmentMap, refractiveRatio, ResourceManager.getTexture(diffuseTexture, true), ResourceManager.getTexture(refractionMap, false));
    }

    public RefractiveMaterial(CubeMap environmentMap, float refractiveRatio, Texture diffuseTexture, Texture refractionMap)
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
        shader.setFloat(refractiveRatio, "refractiveRatio");
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
