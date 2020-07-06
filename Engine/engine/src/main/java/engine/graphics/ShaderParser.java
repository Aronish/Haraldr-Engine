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
    private static final String INCLUDE_PATH = "default_shaders/include/";
    private static final String SHADER_SPLIT_TOKEN = "#shader ";
    private static final int SHADER_TYPE_SPECIFIER_LENGTH = 4;

    public static @NotNull List<Shader.InternalShader> parseShaderSource(@NotNull String source)
    {
        while (source.contains("#include"))
        {
            ///// Preprocess #include /////////////////////
            Map<String, String> includes = new HashMap<>();
            for (int index = source.indexOf(INCLUDE_TOKEN); index != -1; index = source.indexOf(INCLUDE_TOKEN, index + 1))
            {
                int quoteStart = source.indexOf("\"", index + INCLUDE_TOKEN.length());
                int quoteEnd = source.indexOf("\"", quoteStart + 1);
                String replaceToken = source.substring(index, quoteEnd + 1);
                String includePath = source.substring(quoteStart + 1, quoteEnd);
                includes.put(replaceToken, IOUtils.readResource(INCLUDE_PATH + includePath, IOUtils::resourceToString));
            }
            for (String replaceToken : includes.keySet())
            {
                source = source.replaceAll(replaceToken, includes.get(replaceToken));
            }
        }
        List<Shader.InternalShader> internalShaders = new ArrayList<>();
        ///// Split into OpenGL shaders ////////////////////////////
        String[] splitSource = source.split(SHADER_SPLIT_TOKEN);
        for (int string = 1; string < splitSource.length; ++string)
        {
            String type = splitSource[string].substring(0, SHADER_TYPE_SPECIFIER_LENGTH);
            String separatedSource = splitSource[string].substring(SHADER_TYPE_SPECIFIER_LENGTH);
            switch (type)
            {
                case "vert" -> internalShaders.add(new Shader.InternalShader(GL_VERTEX_SHADER, separatedSource));
                case "frag" -> internalShaders.add(new Shader.InternalShader(GL_FRAGMENT_SHADER, separatedSource));
                case "geom" -> internalShaders.add(new Shader.InternalShader(GL_GEOMETRY_SHADER, separatedSource));
                default -> MAIN_LOGGER.error("Unknown shader type " + type + "!");
            }
        }
        return internalShaders;
    }

    public static @NotNull String testParse(@NotNull String source)
    {
        while (source.contains("#include"))
        {
            ///// Preprocess #include /////////////////////
            Map<String, String> includes = new HashMap<>();
            for (int index = source.indexOf(INCLUDE_TOKEN); index != -1; index = source.indexOf(INCLUDE_TOKEN, index + 1))
            {
                int quoteStart = source.indexOf("\"", index + INCLUDE_TOKEN.length());
                int quoteEnd = source.indexOf("\"", quoteStart + 1);
                String replaceToken = source.substring(index, quoteEnd + 1);
                String includePath = source.substring(quoteStart + 1, quoteEnd);
                includes.put(replaceToken, IOUtils.readResource("default_shaders/include/" + includePath, IOUtils::resourceToString));
            }
            for (String replaceToken : includes.keySet())
            {
                source = source.replaceAll(replaceToken, includes.get(replaceToken));
            }
        }
        return source;
    }

    public static @NotNull List<Shader.InternalShader> parseShader(String path)
    {
        String fullSource = IOUtils.readResource(path, IOUtils::resourceToString);
        return parseShaderSource(fullSource);
    }
}