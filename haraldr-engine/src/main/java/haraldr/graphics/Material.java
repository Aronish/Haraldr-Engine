package haraldr.graphics;

import jsonparser.JSONException;
import jsonparser.JSONObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Material
{
    private Shader shader;
    private List<ShaderUniform> uniforms;
    private List<ShaderSampler> samplers;

    @Contract(pure = true)
    public Material(@NotNull Shader shader, @NotNull List<ShaderUniform> uniforms, List<ShaderSampler> samplers)
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

    @Contract("_ -> new")
    public static @NotNull Material create(@NotNull JSONObject materialDefinition) throws JSONException
    {
        return MaterialParser.parseMaterial(materialDefinition);
    }
}
