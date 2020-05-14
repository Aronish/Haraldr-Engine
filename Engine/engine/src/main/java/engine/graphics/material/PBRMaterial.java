package engine.graphics.material;

import engine.graphics.CubeMap;
import engine.graphics.ResourceManager;
import engine.graphics.Shader;
import engine.graphics.Texture;
import engine.math.Vector3f;

@SuppressWarnings({"unused", "WeakerAccess"})
public class PBRMaterial extends Material
{
    private Texture
            albedo = Texture.DEFAULT_WHITE,
            normalMap = Texture.DEFAULT_WHITE,
            metallicMap = Texture.DEFAULT_WHITE,
            roughnessMap = Texture.DEFAULT_WHITE,
            displacementMap = Texture.DEFAULT_WHITE;
    private CubeMap diffuseIrradianceMap, prefilteredMap;
    private Vector3f color = new Vector3f(1f);
    private float metallic = 1f, roughness = 1f;
    private float useParallaxMapping = 0f;

    public PBRMaterial(Vector3f color, float metallic, float roughness, CubeMap environmentMap)
    {
        super(Shader.PBR);
        this.color          = color;
        this.metallic       = metallic;
        this.roughness      = roughness;
        this.diffuseIrradianceMap   = CubeMap.createDiffuseIrradianceMap(environmentMap);
        this.prefilteredMap         = CubeMap.createPrefilteredEnvironmentMap(environmentMap);
    }

    /////TEXTURED///////////////////////////////////////////////////////////////////////////////

    public PBRMaterial(String albedo, String normalMap, String metallicMap, String roughnessMap, CubeMap environmentMap)
    {
        this(ResourceManager.getTexture(albedo, true),
             ResourceManager.getTexture(normalMap, false),
             ResourceManager.getTexture(metallicMap, false),
             ResourceManager.getTexture(roughnessMap, false),
             environmentMap
        );
    }

    public PBRMaterial(Texture albedo, Texture normalMap, Texture metallicMap, Texture roughnessMap, CubeMap environmentMap)
    {
        super(Shader.PBR);
        this.albedo                 = albedo;
        this.normalMap              = normalMap;
        this.metallicMap            = metallicMap;
        this.roughnessMap           = roughnessMap;
        this.diffuseIrradianceMap   = CubeMap.createDiffuseIrradianceMap(environmentMap);
        this.prefilteredMap         = CubeMap.createPrefilteredEnvironmentMap(environmentMap);
    }

    public PBRMaterial(String albedo, String normalMap, String metallicMap, String roughnessMap, String displacementMap, CubeMap environmentMap)
    {
        this(ResourceManager.getTexture(albedo, true),
             ResourceManager.getTexture(normalMap, false),
             ResourceManager.getTexture(metallicMap, false),
             ResourceManager.getTexture(roughnessMap, false),
             ResourceManager.getTexture(displacementMap, false),
             environmentMap
        );
    }

    //TODO: Good idea to allow custom texture objects in the end?
    public PBRMaterial(Texture albedo, Texture normalMap, Texture metallicMap, Texture roughnessMap, Texture displacementMap, CubeMap environmentMap)
    {
        super(Shader.PBR);
        this.albedo                 = albedo;
        this.normalMap              = normalMap;
        this.metallicMap            = metallicMap;
        this.roughnessMap           = roughnessMap;
        this.displacementMap        = displacementMap;
        this.diffuseIrradianceMap   = CubeMap.createDiffuseIrradianceMap(environmentMap);
        this.prefilteredMap         = CubeMap.createPrefilteredEnvironmentMap(environmentMap);
        useParallaxMapping          = 1f;
    }

    public PBRMaterial(String albedo, String normalMap, String metallicMap, String roughnessMap, String displacementMap, Vector3f color, float metallic, float roughness, CubeMap environmentMap)
    {
        this(ResourceManager.getTexture(albedo, true),
             ResourceManager.getTexture(normalMap, false),
             ResourceManager.getTexture(metallicMap, false),
             ResourceManager.getTexture(roughnessMap, false),
             ResourceManager.getTexture(displacementMap, false),
             color, metallic, roughness, environmentMap
         );
    }

    public PBRMaterial(Texture albedo, Texture normalMap, Texture metallicMap, Texture roughnessMap, Texture displacementMap, Vector3f color, float metallic, float roughness, CubeMap environmentMap)
    {
        super(Shader.PBR);
        this.albedo                 = albedo;
        this.normalMap              = normalMap;
        this.metallicMap            = metallicMap;
        this.roughnessMap           = roughnessMap;
        this.displacementMap        = displacementMap;
        this.color                  = color;
        this.metallic               = metallic;
        this.roughness              = roughness;
        this.diffuseIrradianceMap   = CubeMap.createDiffuseIrradianceMap(environmentMap);
        this.prefilteredMap         = CubeMap.createPrefilteredEnvironmentMap(environmentMap);
    }

    public void bind()
    {
        super.bind();
        shader.setVector3f(color, "u_Albedo");
        shader.setFloat(metallic, "u_Metallic");
        shader.setFloat(roughness, "u_Roughness");
        albedo.bind(0);
        normalMap.bind(1);
        metallicMap.bind(2);
        roughnessMap.bind(3);
        displacementMap.bind(4);
        shader.setFloat(useParallaxMapping, "u_UseParallaxMapping");

        diffuseIrradianceMap.bind(5);
        prefilteredMap.bind(6);
        Texture.BRDF_LUT.bind(7);
    }

    public void setMetallic(float metallic)
    {
        this.metallic = metallic;
    }

    public void setRoughness(float roughness)
    {
        this.roughness = roughness;
    }
}
