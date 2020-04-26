package engine.graphics.pbr;

import engine.graphics.Shader;
import engine.math.Vector3f;

public class PBRMaterial
{
    private Vector3f albedo;
    private float metallic, roughness;

    public PBRMaterial(Vector3f albedo, float metallic, float roughness)
    {
        this.albedo = albedo;
        this.metallic = metallic;
        this.roughness = roughness;
    }

    public void bind(Vector3f viewPosition)
    {
        Shader.PBR.bind();
        Shader.PBR.setVector3f(viewPosition, "viewPosition");
        Shader.PBR.setVector3f(albedo, "albedo");
        Shader.PBR.setFloat(metallic, "metallic");
        Shader.PBR.setFloat(roughness, "roughness");
    }

    public void setMetallic(float metallic)
    {
        this.metallic = metallic;
    }

    public void setRoughness(float roughness)
    {
        this.roughness = roughness;
    }

    public float getMetallic()
    {
        return metallic;
    }

    public float getRoughness()
    {
        return roughness;
    }
}
