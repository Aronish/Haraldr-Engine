package engine.graphics;

import engine.main.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static engine.main.Application.MAIN_LOGGER;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

public class ShaderParser
{
    private static final String INCLUDE_TOKEN = "#include ";
    private static final String SHADER_SPLIT_TOKEN = "#shader ";
    private static final int SHADER_TYPE_SPECIFIER_LENGTH = 4;

    public static @NotNull List<Shader.InternalShader> parseShader(String path)
    {
        ///// Read full source file ////////////////////////////////////
        List<Shader.InternalShader> internalShaders = new ArrayList<>();
        String fullSource = IOUtils.readResource(path, IOUtils::resourceToString);
        assert fullSource != null : "Shader#Shader(String)#fullSource should never be null!";
        ///// Preprocess #include /////////////////////
        Map<String, String> includes = new HashMap<>();
        for (int index = fullSource.indexOf(INCLUDE_TOKEN); index != -1; index = fullSource.indexOf(INCLUDE_TOKEN, index + 1))
        {
            int quoteStart = fullSource.indexOf("\"", index + INCLUDE_TOKEN.length());
            int quoteEnd = fullSource.indexOf("\"", quoteStart + 1);
            String replaceToken = fullSource.substring(index, quoteEnd + 1);
            String includePath = fullSource.substring(quoteStart + 1, quoteEnd);
            includes.put(replaceToken, IOUtils.readResource("default_shaders/include/" + includePath, IOUtils::resourceToString));
        }
        for (String replaceToken : includes.keySet())
        {
            fullSource = fullSource.replaceAll(replaceToken, includes.get(replaceToken));
        }
        ///// Split into OpenGL shaders ////////////////////////////
        String[] splitSource = fullSource.split(SHADER_SPLIT_TOKEN);
        for (int string = 1; string < splitSource.length; ++string)
        {
            String type = splitSource[string].substring(0, SHADER_TYPE_SPECIFIER_LENGTH);
            String source = splitSource[string].substring(SHADER_TYPE_SPECIFIER_LENGTH);
            switch (type)
            {
                case "vert":
                    internalShaders.add(new Shader.InternalShader(GL_VERTEX_SHADER, source));
                    break;
                case "frag":
                    internalShaders.add(new Shader.InternalShader(GL_FRAGMENT_SHADER, source));
                    break;
                case "geom":
                    internalShaders.add(new Shader.InternalShader(GL_GEOMETRY_SHADER, source));
                    break;
                default:
                    MAIN_LOGGER.error("Unknown shader type " + type + "!");
                    break;
            }
        }
        return internalShaders;
    }
}