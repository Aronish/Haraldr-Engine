package haraldr.graphics;

import haraldr.main.IOUtils;
import haraldr.math.Vector2f;
import haraldr.math.Vector3f;
import haraldr.math.Vector4f;
import jsonparser.JSONArray;
import jsonparser.JSONException;
import jsonparser.JSONObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.GL_ACTIVE_UNIFORMS;
import static org.lwjgl.opengl.GL20.GL_FLOAT_VEC2;
import static org.lwjgl.opengl.GL20.GL_FLOAT_VEC3;
import static org.lwjgl.opengl.GL20.GL_FLOAT_VEC4;
import static org.lwjgl.opengl.GL20.glGetActiveUniform;
import static org.lwjgl.opengl.GL20.glGetProgramiv;

public class MaterialParser
{
    private static final JSONObject specification = new JSONObject(IOUtils.readResource("default_models/material_specification.json", IOUtils::resourceToString));

    private static final String DIFF_IRR_TOKEN = "_DIFF_IRR", PREFILTERED_TOKEN = "_PREF";

    @Contract("_ -> new")
    public static @NotNull Material parseMaterial(@NotNull JSONObject materialDefinition) throws JSONException
    {
        String type = materialDefinition.getString("type");
        JSONObject materialProperties = materialDefinition.getJSONObject("properties");

        JSONObject variant;
        if (specification.getJSONObject(type).has("variants"))
        {
            variant = getBestVariantCandidate(materialProperties, specification.getJSONObject(type).getJSONObject("variants"));
        } else
        {
            variant = specification.getJSONObject(type);
        }

        // Extract samplers and optional switches
        List<String> switches = new ArrayList<>();
        List<ShaderSampler> shaderSamplers = variant.has("samplers") ? extractSamplers(materialProperties, variant.getJSONObject("samplers"), switches) : new ArrayList<>();

        // Setup shader and uniforms
        if (variant.has("switches"))
        {
            switches.addAll(JSONArrayToStringList(variant.getJSONArray("switches")));
        }
        Shader shader;
        if (switches.isEmpty())
        {
            shader = Shader.create(variant.getString("shader"));
        } else
        {
            shader = Shader.createShaderWithSwitches(variant.getString("shader"), switches);
        }
        List<ShaderUniform> shaderUniforms = extractUniforms(
                shader.getProgramHandle(), materialProperties,
                variant.has("optional") ? JSONArrayToStringList(variant.getJSONArray("optional")) : new ArrayList<>()
        );

        return new Material(shader, shaderUniforms, shaderSamplers);
    }

    /**
     * If a material specification has multiple variants, the most accurate one will be picked according to the arguments present in the material file.
     */
    private static JSONObject getBestVariantCandidate(JSONObject materialProperties, JSONObject variants)
    {
        Map<String, Float> counts = new HashMap<>();
        for (String variantToken : variants.keySet())
        {
            JSONObject variant = variants.getJSONObject(variantToken);
            String[] dependants = JSONArrayToStringArray(variant.getJSONArray("dependants"));
            // Count hits in material file
            for (String dependant : dependants)
            {
                if (materialProperties.has(dependant))
                {
                    counts.put(variantToken, counts.getOrDefault(variantToken, 0f) + 1f);
                }
            }
            // Calculate accuracy percentage (hits / total dependants)
            if (counts.containsKey(variantToken))
            {
                counts.put(variantToken, counts.get(variantToken) / dependants.length);
            }
        }
        if (counts.isEmpty()) throw new JSONException("No variant found!");
        Map.Entry<String, Float> bestCandidate = Collections.max(counts.entrySet(), Map.Entry.comparingByValue());
        return variants.getJSONObject(bestCandidate.getKey());
    }

    /**
     * Extracts both 2D textures and cubemaps from the material definition. If there are optional ones, optionalSamplerSwitches will be populated
     * with the correct compiler switch.
     *
     * Cube map "extensions" for PBR should be stored in a folder with the same name as
     * the original .hdr file. The diffuse irradiance map should have the suffix _DIFF_IRR and
     * the prefiltered environment map should have _PREF. The format is .exr and so is the file extension.
     */
    private static @NotNull List<ShaderSampler> extractSamplers(JSONObject materialProperties, JSONObject samplerDefinition, List<String> optionalSamplerSwitches)
    {
        List<ShaderSampler> samplers = new ArrayList<>();
        for (String samplerToken : samplerDefinition.keySet())
        {
            JSONObject sampler = samplerDefinition.getJSONObject(samplerToken);
            if (sampler.has("optional_switch"))
            {
                if (!materialProperties.has(samplerToken)) continue;
                optionalSamplerSwitches.add(sampler.getString("optional_switch"));
            }
            if (sampler.has("extended")) // Checks for cubemap or 2D texture. ('extended' is only a part of cubemaps)
            {
                if (sampler.getBoolean("extended")) // Be aware that this occupies 3 sampler units.
                {
                    String folderPath = materialProperties.getString("environment_map");
                    String hdrFileName = folderPath.substring(folderPath.lastIndexOf("/"));

                    samplers.add(new ShaderSampler.CubeMap(CubeMap.createDiffuseIrradianceMap(IOUtils.getAbsolutePath("/" + folderPath + hdrFileName + DIFF_IRR_TOKEN + ".exr")), 0));
                    samplers.add(new ShaderSampler.CubeMap(CubeMap.createPrefilteredEnvironmentMap(IOUtils.getAbsolutePath("/" + folderPath + hdrFileName + PREFILTERED_TOKEN + ".exr")), 1));
                    samplers.add(new ShaderSampler.Texture2D(Texture.BRDF_LUT, 2));
                } else
                {
                    CubeMap environmentMap = CubeMap.createEnvironmentMap(materialProperties.getString("environment_map"));
                    samplers.add(new ShaderSampler.CubeMap(environmentMap, 0));
                }
            } else
            {
                if (!materialProperties.has(samplerToken)) throw new JSONException("Missing material property : " + samplerToken);
                String path = materialProperties.getString(samplerToken);
                samplers.add(new ShaderSampler.Texture2D(Texture.create(path, sampler.getBoolean("color_data")), sampler.getInt("unit")));
            }
        }
        return samplers;
    }

    /**
     * Extracts all uniforms from the now compiled shader object where missing optional arguments using the default set in the shader.
     */
    private static @NotNull List<ShaderUniform> extractUniforms(int shaderHandle, JSONObject materialProperties, List<String> optionalArguments)
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
                    if (!materialProperties.has(jsonName) && optionalArguments.contains(jsonName)) continue;
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

    private static String[] JSONArrayToStringArray(JSONArray jsonArray)
    {
        String[] strings = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); ++i)
        {
            strings[i] = jsonArray.getString(i);
        }
        return strings;
    }

    private static List<String> JSONArrayToStringList(JSONArray jsonArray)
    {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i)
        {
            strings.add(jsonArray.getString(i));
        }
        return strings;
    }
}