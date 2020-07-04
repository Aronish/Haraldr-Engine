package engine;

import engine.graphics.ShaderBuilder;
import engine.graphics.ShaderDataType;
import engine.graphics.ShaderParser;

public class ShaderBuilderTest
{
    public static void main(String[] args)
    {
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

        //System.out.println(shaderBuilder.toShaderFile());

        System.out.println(ShaderParser.testParse(shaderBuilder.toShaderFile()));
    }
}