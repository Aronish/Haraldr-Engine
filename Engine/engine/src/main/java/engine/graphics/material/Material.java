package engine.graphics.material;

import engine.graphics.*;
import engine.math.Vector3f;
import main.JSONObject;
import org.jetbrains.annotations.NotNull;

public abstract class Material
{
    protected Shader shader;

    public Material()
    {
    }

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

    public static Material parseMaterial(String path, @NotNull JSONObject materialDefinition)
    {
        JSONObject materialProperties = materialDefinition.getJSONObject("properties");
        return switch (materialDefinition.getString("type"))
        {
            case "PBR_UNTEXTURED" -> {
                Vector3f albedo = new Vector3f(materialProperties.getJSONArray("albedo"));
                float metalness = (float) materialProperties.getDouble("metalness");
                float roughness = (float) materialProperties.getDouble("roughness");
                CubeMap environmentMap = CubeMap.createEnvironmentMap(materialProperties.getString("environment_map"));
                yield new PBRMaterial(albedo, metalness, roughness, environmentMap);
            }
            case "PBR_TEXTURED" -> {
                Texture albedo = ResourceManager.getTexture(materialProperties.getString("albedo"), true);
                Texture normal = ResourceManager.getTexture(materialProperties.getString("normal"), false);
                Texture metalness = ResourceManager.getTexture(materialProperties.getString("metalness"), false);
                Texture roughness = ResourceManager.getTexture(materialProperties.getString("roughness"), false);
                CubeMap environmentMap = CubeMap.createEnvironmentMap(materialProperties.getString("environment_map"));
                yield new PBRMaterial(albedo, normal, metalness, roughness, environmentMap);
            }
            case "PBR_TEXTURED_HEIGHT" -> {
                Texture albedo = ResourceManager.getTexture(materialProperties.getString("albedo"), true);
                Texture normal = ResourceManager.getTexture(materialProperties.getString("normal"), false);
                Texture metalness = ResourceManager.getTexture(materialProperties.getString("metalness"), false);
                Texture roughness = ResourceManager.getTexture(materialProperties.getString("roughness"), false);
                Texture displacementMap = ResourceManager.getTexture(materialProperties.getString("displacement_map"), false);
                CubeMap environmentMap = CubeMap.createEnvironmentMap(materialProperties.getString("environment_map"));
                yield new PBRMaterial(albedo, normal, metalness, roughness, displacementMap, environmentMap);
            }
            case "DIFFUSE_UNTEXTURED" -> {
                Vector3f diffuseColor = new Vector3f(materialProperties.getJSONArray("diffuse_color"));
                float diffuseStrength = (float) materialProperties.getDouble("diffuse_strength");
                float specularStrength = (float) materialProperties.getDouble("specular_strength");
                float specularExponent = (float) materialProperties.getDouble("specular_exponent");
                float opacity = (float) materialProperties.getDouble("opacity");
                //yield new DiffuseMaterial(diffuseColor, diffuseStrength, specularStrength, specularExponent, opacity);

                ShaderBuilder shaderBuilder = new ShaderBuilder();
                shaderBuilder.beginShader(ShaderBuilder.ShaderType.VERTEX, ShaderBuilder.GLSLVersion.CORE_460);
                shaderBuilder.addVertexAttribute(0, ShaderDataType.FLOAT3, "a_Position");
                shaderBuilder.addVertexAttribute(1, ShaderDataType.FLOAT3, "a_Normal");
                shaderBuilder.addVertexAttribute(2, ShaderDataType.FLOAT2, "a_TextureCoordinate");
                shaderBuilder.addVertexOutput(ShaderBuilder.VertexOutput.NORMAL_WORLD);
                shaderBuilder.addVertexOutput(ShaderBuilder.VertexOutput.POSITION_WORLD);
                shaderBuilder.addVertexOutput(ShaderBuilder.VertexOutput.TEXTURE_COORDINATE);
                shaderBuilder.endShader(ShaderBuilder.ShaderType.VERTEX);

                shaderBuilder.addPresetFragmentShader(ShaderBuilder.LightingModel.DIFFUSE_FLAT);
                //yield Shader.createFromSource(path, shaderBuilder.toShaderFile());
                //yield new TestMaterial(Shader.createFromSource(path, shaderBuilder.toShaderFile()));
                yield new DiffuseMaterial(Shader.createFromSource(path, shaderBuilder.toShaderFile()) ,diffuseColor, diffuseStrength, specularStrength, specularExponent, opacity);
            }
            case "DIFFUSE_TEXTURED" -> {
                Texture diffuseTexture = ResourceManager.getTexture(materialProperties.getString("diffuse_texture"), true);
                float diffuseStrength = (float) materialProperties.getDouble("diffuse_strength");
                float specularStrength = (float) materialProperties.getDouble("specular_strength");
                float specularExponent = (float) materialProperties.getDouble("specular_exponent");
                float opacity = (float) materialProperties.getDouble("opacity");
                //yield new DiffuseMaterial(diffuseTexture, diffuseStrength, specularStrength, specularExponent, opacity);

                ShaderBuilder shaderBuilder = new ShaderBuilder();
                shaderBuilder.beginShader(ShaderBuilder.ShaderType.VERTEX, ShaderBuilder.GLSLVersion.CORE_460);
                shaderBuilder.addVertexAttribute(0, ShaderDataType.FLOAT3, "a_Position");
                shaderBuilder.addVertexAttribute(1, ShaderDataType.FLOAT3, "a_Normal");
                shaderBuilder.addVertexAttribute(2, ShaderDataType.FLOAT2, "a_TextureCoordinate");
                shaderBuilder.addVertexOutput(ShaderBuilder.VertexOutput.NORMAL_WORLD);
                shaderBuilder.addVertexOutput(ShaderBuilder.VertexOutput.POSITION_WORLD);
                shaderBuilder.addVertexOutput(ShaderBuilder.VertexOutput.TEXTURE_COORDINATE);
                shaderBuilder.endShader(ShaderBuilder.ShaderType.VERTEX);

                shaderBuilder.addPresetFragmentShader(ShaderBuilder.LightingModel.DIFFUSE_FLAT);
                yield new DiffuseMaterial(Shader.createFromSource(path, shaderBuilder.toShaderFile()), diffuseTexture, diffuseStrength, specularStrength, specularExponent, opacity);
            }
            case "NORMAL" -> {
                Texture diffuseTexture = ResourceManager.getTexture(materialProperties.getString("diffuse_texture"), true);
                Texture normalMap = ResourceManager.getTexture(materialProperties.getString("normal_map"), false);
                float specularStrength = (float) materialProperties.getDouble("specular_strength");
                float specularExponent = (float) materialProperties.getDouble("specular_exponent");
                float opacity = (float) materialProperties.getDouble("opacity");
                yield new NormalMaterial(diffuseTexture, normalMap, specularStrength, specularExponent, opacity);
            }
            case "REFLECTIVE" -> {
                Texture diffuseTexture = ResourceManager.getTexture(materialProperties.getString("diffuse_texture"), true);
                Texture reflectionMap = ResourceManager.getTexture(materialProperties.getString("reflection_map"), false);
                CubeMap environmentMap = CubeMap.createEnvironmentMap(materialProperties.getString("environment_map"));
                yield new ReflectiveMaterial(diffuseTexture, reflectionMap, environmentMap);
            }
            case "REFRACTIVE" -> {
                Texture diffuseTexture = ResourceManager.getTexture(materialProperties.getString("diffuse_texture"), true);
                Texture refractionMap = ResourceManager.getTexture(materialProperties.getString("refraction_map"), false);
                CubeMap environmentMap = CubeMap.createEnvironmentMap(materialProperties.getString("environment_map"));
                float refractiveRatio = (float) materialProperties.getDouble("refractive_ratio_begin") / (float) materialProperties.getDouble("refractive_ratio_end");
                yield new RefractiveMaterial(diffuseTexture, refractionMap, refractiveRatio, environmentMap);
            }
            default -> throw new IllegalStateException("Unknown material type" + materialDefinition.getString("type") + "!");
        };
    }
}