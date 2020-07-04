package engine.graphics;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShaderBuilder
{
    private StringBuilder shaderFile = new StringBuilder();
    private List<VertexOutput> vertexOutputs = new ArrayList<>();
    private boolean needsNormalMatrix;

    ///// COMMON ///////////////////////////////////////////////////////////////

    public void beginShader(@NotNull ShaderType shaderType, @NotNull GLSLVersion version)
    {
        shaderFile.append(String.format("#shader %s#version %s\n", shaderType.token, version.token));
        if (shaderType == ShaderType.FRAGMENT)
        {
            beginFragmentShader();
        }
    }

    public void endShader(@NotNull ShaderType shaderType)
    {
        switch (shaderType)
        {
            case VERTEX -> endVertexShader();
            case FRAGMENT -> endFragmentShader();
        }
    }

    ///// VERTEX SHADER /////////////////////////////////////////////////////////////////////////////

    public void addVertexAttribute(int location, @NotNull ShaderDataType shaderDataType, String name)
    {
        shaderFile.append(String.format("layout (location = %d) in %s %s;", location, shaderDataType.token, name));
    }

    public void addVertexOutput(@NotNull VertexOutput vertexOutput)
    {
        if (vertexOutput.equals(VertexOutput.NORMAL_WORLD)) needsNormalMatrix = true;
        vertexOutputs.add(vertexOutput);
        shaderFile.append(String.format("out %s %s;", vertexOutput.dataType.token, vertexOutput.name));
    }

    private void endVertexShader()
    {
        shaderFile.append("uniform mat4 model = mat4(1.0f);" +
                "layout (std140, binding = 0) uniform matrices" +
                "{" +
                "    mat4 view;" +
                "    mat4 projection;" +
                "};");
        shaderFile.append("void main(void){");
        if (needsNormalMatrix) shaderFile.append("mat3 normalMatrix = mat3(model);");
        for (VertexOutput vertexOutput : vertexOutputs)
        {
            switch (vertexOutput)
            {
                case NORMAL_WORLD -> shaderFile.append("v_Normal_W = normalMatrix * a_Normal;");
                case POSITION_WORLD -> shaderFile.append("v_Position_W = (model * vec4(a_Position, 1.0f)).xyz;");
                case TEXTURE_COORDINATE -> shaderFile.append("v_TextureCoordinate = a_TextureCoordinate;");
            }
        }
        shaderFile.append("gl_Position = projection * view * model * vec4(a_Position, 1.0f);");
        shaderFile.append("}");
    }

    ///// FRAGMENT SHADER //////////////////////////

    private void beginFragmentShader()
    {
        for (VertexOutput vertexOutput : vertexOutputs)
        {
            shaderFile.append(String.format("in %s %s;", vertexOutput.dataType.token, vertexOutput.name));
        }
    }

    private void endFragmentShader()
    {
    }

    public String toShaderFile()
    {
        return shaderFile.toString();
    }

    public void addPresetFragmentShader(@NotNull LightingModel lightingModel)
    {
        switch (lightingModel)
        {
            case DIFFUSE_FLAT -> shaderFile.append("#include \"diffuse_flat.glsl\"");
            case DIFFUSE_TEXTURED -> shaderFile.append("#include \"diffuse_textured.glsl\"");
        }
    }

    public enum GLSLVersion
    {
        CORE_460("460 core");

        private final String token;

        GLSLVersion(String token)
        {
            this.token = token;
        }
    }

    public enum ShaderType
    {
        VERTEX("vert"), FRAGMENT("frag");

        public final String token;

        ShaderType(String token)
        {
            this.token = token;
        }
    }

    public enum VertexOutput
    {
        NORMAL_WORLD("v_Normal_W", ShaderDataType.FLOAT3),
        POSITION_WORLD("v_Position_W", ShaderDataType.FLOAT3),
        TEXTURE_COORDINATE("v_TextureCoordinate", ShaderDataType.FLOAT2);

        public final String name;
        public final ShaderDataType dataType;

        VertexOutput(String name, ShaderDataType dataType)
        {
            this.name = name;
            this.dataType = dataType;
        }
    }

    public enum LightingModel
    {
        DIFFUSE_FLAT, DIFFUSE_TEXTURED
    }
}