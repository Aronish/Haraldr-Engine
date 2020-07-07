package engine.graphics;

import engine.main.IOUtils;
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
        for (int unit = 0; unit < samplers.size(); ++unit)
        {
            samplers.get(unit).bind(unit);
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

    ///// PARSER ////////////////////////////////////////////////////////////

    public static JsonMaterial create(@NotNull JSONObject materialDefinition) throws JSONException
    {
        return switch (materialDefinition.getJSONObject("shader").getString("type"))
        {
            case "PBR" -> {
                JSONObject materialProperties = materialDefinition.getJSONObject("properties");
                Shader shader;

                if (materialDefinition.getJSONObject("shader").getJSONObject("properties").getBoolean("textured"))
                {
                    if (materialDefinition.getJSONObject("shader").getJSONObject("properties").getBoolean("parallax_map"))
                    {
                        shader = createShaderWithSwitches("default_shaders/pbr_textured.glsl", "PARALLAX_MAP");
                    } else
                    {
                        shader = Shader.create("default_shaders/pbr_textured.glsl");
                    }
                } else
                {
                    shader = Shader.create("default_shaders/pbr_untextured.glsl");
                }

                List<ShaderSampler> samplers = extractSamplers(shader.getProgramHandle(), materialProperties);
                CubeMap environmentMap = CubeMap.createEnvironmentMap(materialProperties.getString("environment_map"));
                samplers.add(0, new ShaderSampler.Texture2D(Texture.BRDF_LUT));
                samplers.add(0, new ShaderSampler.CubeMap(CubeMap.createPrefilteredEnvironmentMap(environmentMap)));
                samplers.add(0, new ShaderSampler.CubeMap(CubeMap.createDiffuseIrradianceMap(environmentMap)));

                yield new JsonMaterial(shader, extractUniforms(shader.getProgramHandle(), materialProperties), samplers);
            }
            case "REFLECTIVE" -> {
                JSONObject materialProperties = materialDefinition.getJSONObject("properties");
                Shader shader = Shader.create("default_shaders/reflective.glsl");

                List<ShaderSampler> samplers = extractSamplers(shader.getProgramHandle(), materialProperties);
                samplers.add(0, new ShaderSampler.CubeMap(CubeMap.createEnvironmentMap(materialProperties.getString("environment_map"))));

                yield new JsonMaterial(shader, extractUniforms(shader.getProgramHandle(), materialProperties), samplers);
            }
            case "REFRACTIVE" -> {
                JSONObject materialProperties = materialDefinition.getJSONObject("properties");
                Shader shader = Shader.create("default_shaders/refractive.glsl");

                List<ShaderSampler> samplers = extractSamplers(shader.getProgramHandle(), materialProperties);
                samplers.add(0, new ShaderSampler.CubeMap(CubeMap.createEnvironmentMap(materialProperties.getString("environment_map"))));

                yield new JsonMaterial(shader, extractUniforms(shader.getProgramHandle(), materialProperties), samplers);
            }
            case "SIMPLE" -> {
                JSONObject materialProperties = materialDefinition.getJSONObject("properties");
                JSONObject shaderProperties = materialDefinition.getJSONObject("shader").getJSONObject("properties");
                Shader shader;

                if (shaderProperties.getBoolean("textured"))
                {
                    if (shaderProperties.getBoolean("normal_map"))
                    {
                        shader = Shader.create("default_shaders/normal.glsl");
                    } else
                    {
                        shader = createShaderWithSwitches("default_shaders/diffuse.glsl", "TEXTURED");
                    }
                    yield new JsonMaterial(shader, extractUniforms(shader.getProgramHandle(), materialProperties), extractSamplers(shader.getProgramHandle(), materialProperties));
                } else
                {
                    shader = Shader.create("default_shaders/diffuse.glsl");
                    yield new JsonMaterial(shader, extractUniforms(shader.getProgramHandle(), materialProperties), new ArrayList<>());
                }
            }
            case "UNLIT" -> {
                JSONObject materialProperties = materialDefinition.getJSONObject("properties");
                JSONObject shaderProperties = materialDefinition.getJSONObject("shader").getJSONObject("properties");
                Shader shader;

                if (shaderProperties.getBoolean("textured"))
                {
                    shader = createShaderWithSwitches("default_shaders/unlit.glsl", "TEXTURED");
                    yield new JsonMaterial(shader, extractUniforms(shader.getProgramHandle(), materialProperties), extractSamplers(shader.getProgramHandle(), materialProperties));
                } else
                {
                    shader = Shader.create("default_shaders/unlit.glsl");
                    yield new JsonMaterial(shader, extractUniforms(shader.getProgramHandle(), materialProperties), new ArrayList<>());
                }
            }
            default -> throw new JSONException("Unknown shader type!");
        };
    }

    private static Shader createShaderWithSwitches(String path, @NotNull String... switches)
    {
        StringBuilder key = new StringBuilder(path);
        for (String define : switches) key.append(define);
        return Shader.createFromSource(key.toString(), IOUtils.readResource(path, IOUtils::resourceToString), switches);
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

    private static @NotNull List<ShaderSampler> extractSamplers(int shaderHandle, JSONObject materialProperties)
    {
        List<ShaderSampler> samplers = new ArrayList<>();
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
                if (name.contains("map_"))
                {
                    String jsonName = name.substring(6).toLowerCase();
                    samplers.add(new ShaderSampler.Texture2D(Texture.create(
                            materialProperties.getJSONObject(jsonName).getString("path"),
                            materialProperties.getJSONObject(jsonName).getBoolean("color"))
                    ));
                }
            }
        }
        return samplers;
    }
}
