package engine.graphics;

import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.math.Vector4f;
import jsonparser.JSONException;
import jsonparser.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.GL_ACTIVE_UNIFORMS;
import static org.lwjgl.opengl.GL20.GL_FLOAT_VEC2;
import static org.lwjgl.opengl.GL20.GL_FLOAT_VEC3;
import static org.lwjgl.opengl.GL20.GL_FLOAT_VEC4;
import static org.lwjgl.opengl.GL20.glGetActiveUniform;
import static org.lwjgl.opengl.GL20.glGetProgramiv;

public class JsonMaterial
{
    private Shader shader;
    private List<ShaderUniform> uniforms;
    private List<ShaderSampler> samplers;

    private JsonMaterial(@NotNull Shader shader, @NotNull List<ShaderUniform> uniforms, List<ShaderSampler> samplers)
    {
        this.shader = shader;
        this.uniforms = uniforms;
        this.samplers = samplers;
    }

    public void bind()
    {
        shader.bind();
        for (ShaderUniform uniform : uniforms)
        {
            uniform.bind(shader);
        }
        for (ShaderSampler sampler : samplers)
        {
            sampler.bind();
        }
    }

    public void unbind()
    {
        shader.unbind();
    }

    public Shader getShader()
    {
        return shader;
    }

    ///// PARSER /////////////////////////////////////////////////////////////////////////////////

    public static JsonMaterial create(@NotNull JSONObject materialDefinition) throws JSONException
    {
        return switch (materialDefinition.getJSONObject("shader").getString("type"))
        {
            case "PBR" -> {
                JSONObject materialProperties = materialDefinition.getJSONObject("properties");
                Shader shader;
                List<ShaderSampler> samplers;

                if (materialProperties.has("color"))
                {
                    shader = Shader.create("default_shaders/pbr_untextured.glsl");
                    samplers = new ArrayList<>();
                } else if (materialProperties.has("albedo"))
                {
                    //Set appropriate compile time switches
                    List<String> switches = new ArrayList<>();
                    if (materialProperties.has("displacement")) switches.add("PARALLAX_MAP");
                    if (materialProperties.has("ao")) switches.add("AMBIENT_OCCLUSION");
                    if (switches.isEmpty())
                    {
                        shader = Shader.create("default_shaders/pbr_textured.glsl");
                    }else
                    {
                        shader = Shader.createShaderWithSwitches("default_shaders/pbr_textured.glsl", switches.toArray(new String[0]));
                    }
                    //Extract samplers
                    samplers = extractSamplers(materialProperties, MaterialSamplerDefinition.PBR_TEXTURED);
                } else
                {
                    throw new IllegalArgumentException("Shader type 'PBR' requires either 'albedo' or 'color'");
                }

                CubeMap environmentMap = CubeMap.createEnvironmentMap(materialProperties.getString("environment_map"));
                samplers.add(new ShaderSampler.CubeMap(CubeMap.createDiffuseIrradianceMap(environmentMap), 0));
                samplers.add(new ShaderSampler.CubeMap(CubeMap.createPrefilteredEnvironmentMap(environmentMap), 1));
                samplers.add(new ShaderSampler.Texture2D(Texture.BRDF_LUT, 2));

                yield new JsonMaterial(shader, extractUniforms(shader.getProgramHandle(), materialProperties), samplers);
            }
            case "REFLECTIVE" -> {
                JSONObject materialProperties = materialDefinition.getJSONObject("properties");
                Shader shader = Shader.create("default_shaders/reflective.glsl");

                List<ShaderSampler> samplers = extractSamplers(materialProperties, MaterialSamplerDefinition.REFLECTIVE);
                samplers.add(new ShaderSampler.CubeMap(CubeMap.createEnvironmentMap(materialProperties.getString("environment_map")), 0));

                yield new JsonMaterial(shader, extractUniforms(shader.getProgramHandle(), materialProperties), samplers);
            }
            case "REFRACTIVE" -> {
                JSONObject materialProperties = materialDefinition.getJSONObject("properties");
                Shader shader = Shader.create("default_shaders/refractive.glsl");

                List<ShaderSampler> samplers = extractSamplers(materialProperties, MaterialSamplerDefinition.REFRACTIVE);
                samplers.add(0, new ShaderSampler.CubeMap(CubeMap.createEnvironmentMap(materialProperties.getString("environment_map")), 0));

                yield new JsonMaterial(shader, extractUniforms(shader.getProgramHandle(), materialProperties), samplers);
            }
            case "SIMPLE" -> {
                JSONObject materialProperties = materialDefinition.getJSONObject("properties");
                Shader shader;
                List<ShaderSampler> samplers;

                if (materialProperties.has("diffuse_texture"))
                {
                    if (materialProperties.has("normal_map"))
                    {
                        shader = Shader.create("default_shaders/normal.glsl");
                    } else
                    {
                        shader = Shader.createShaderWithSwitches("default_shaders/diffuse.glsl", "TEXTURED");
                    }
                    samplers = extractSamplers(materialProperties, MaterialSamplerDefinition.SIMPLE);
                } else if (materialProperties.has("diffuse_color"))
                {
                    shader = Shader.create("default_shaders/diffuse.glsl");
                    samplers = new ArrayList<>();
                } else
                {
                    throw new IllegalStateException("Simple shader requires either 'diffuse_texture' or 'diffuse_color'");
                }
                yield new JsonMaterial(shader, extractUniforms(shader.getProgramHandle(), materialProperties), samplers);
            }
            case "UNLIT" -> {
                JSONObject materialProperties = materialDefinition.getJSONObject("properties");
                Shader shader;
                List<ShaderSampler> samplers;

                if (materialProperties.has("texture"))
                {
                    shader = Shader.createShaderWithSwitches("default_shaders/unlit.glsl", "TEXTURED");
                    samplers = extractSamplers(materialProperties, MaterialSamplerDefinition.UNLIT);
                } else
                {
                    shader = Shader.create("default_shaders/unlit.glsl");
                    samplers = new ArrayList<>();
                }
                yield new JsonMaterial(shader, extractUniforms(shader.getProgramHandle(), materialProperties), samplers);
            }
            default -> throw new JSONException("Unknown shader type!");
        };
    }

    private static @NotNull List<ShaderUniform> extractUniforms(int shaderHandle, JSONObject materialProperties)
    {
        List<ShaderUniform> uniforms = new ArrayList<>();

        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer pCount = stack.mallocInt(1);
            glGetProgramiv(shaderHandle, GL_ACTIVE_UNIFORMS, pCount);
            int count = pCount.get();

            IntBuffer size = stack.mallocInt(1);
            for (int i = 0; i < count; ++i)
            {
                IntBuffer type = stack.mallocInt(1);
                String name = glGetActiveUniform(shaderHandle, i, 32, size, type);
                if (name.contains("u_")) // Removes prefix to match json names.
                {
                    String jsonName = name.substring(2).toLowerCase();
                    switch (type.get())
                    {
                        case GL_FLOAT -> uniforms.add(new ShaderUniform.Float(name, materialProperties.getDouble(jsonName)));
                        case GL_FLOAT_VEC2 -> uniforms.add(new ShaderUniform.Vector2f(name, new Vector2f(materialProperties.getJSONArray(jsonName))));
                        case GL_FLOAT_VEC3 -> uniforms.add(new ShaderUniform.Vector3f(name, new Vector3f(materialProperties.getJSONArray(jsonName))));
                        case GL_FLOAT_VEC4 -> uniforms.add(new ShaderUniform.Vector4f(name, new Vector4f(materialProperties.getJSONArray(jsonName))));
                    }
                }
            }
        }
        return uniforms;
    }

    private static @NotNull List<ShaderSampler> extractSamplers(JSONObject materialProperties, @NotNull MaterialSamplerDefinition materialSamplerDefinition)
    {
        List<ShaderSampler> samplers = new ArrayList<>();
        for (MaterialSamplerDefinition.SamplerToken samplerToken : materialSamplerDefinition.samplerTokens)
        {
            if (samplerToken.optional)
            {
                if (!materialProperties.has(samplerToken.token)) continue;
            } else
            {
                if (!materialProperties.has(samplerToken.token))
                {
                    throw new IllegalStateException("Missing material property: " + samplerToken.token);
                }
            }
            String path = materialProperties.getString(samplerToken.token);
            samplers.add(new ShaderSampler.Texture2D(Texture.create(path, samplerToken.colorData), samplerToken.unit));
        }
        return samplers;
    }
}
