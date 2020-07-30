package haraldr.graphics;

import jsonparser.JSONException;
import jsonparser.JSONObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JsonMaterial
{
    private Shader shader;
    private List<ShaderUniform> uniforms;
    private List<ShaderSampler> samplers;

    public JsonMaterial(@NotNull Shader shader, @NotNull List<ShaderUniform> uniforms, List<ShaderSampler> samplers)
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

    public static JsonMaterial create(@NotNull JSONObject materialDefinition) throws JSONException
    {
        return MaterialParser.parseMaterial(materialDefinition);
    }
}
