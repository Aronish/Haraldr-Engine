package engine.graphics;

import engine.main.ArrayUtils;
import engine.main.EntryPoint;
import engine.main.IOUtils;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.math.Vector4f;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static engine.main.Application.MAIN_LOGGER;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgramiv;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderiv;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL46.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL46.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL46.glAttachShader;
import static org.lwjgl.opengl.GL46.glCompileShader;
import static org.lwjgl.opengl.GL46.glCreateProgram;
import static org.lwjgl.opengl.GL46.glCreateShader;
import static org.lwjgl.opengl.GL46.glDeleteShader;
import static org.lwjgl.opengl.GL46.glLinkProgram;
import static org.lwjgl.opengl.GL46.glShaderSource;
import static org.lwjgl.opengl.GL46.glUseProgram;
import static org.lwjgl.opengl.GL46.glValidateProgram;

@SuppressWarnings("unused")
public class Shader
{
    private static final String SPLIT_TOKEN = "#shader ";
    private static final int TYPE_SPECIFIER_LENGTH = 4;

    public static final Shader DEFAULT2D            = new Shader("default_shaders/default2D.glsl");
    public static final Shader DIFFUSE              = new Shader("default_shaders/diffuse.glsl");
    public static final Shader NORMAL               = new Shader("default_shaders/normal.glsl");
    public static final Shader LIGHT_SHADER         = new Shader("default_shaders/simple_color.glsl");
    public static final Shader VISIBLE_NORMALS      = new Shader("default_shaders/visible_normals.glsl");
    public static final Shader REFLECTIVE           = new Shader("default_shaders/reflective.glsl");
    public static final Shader REFRACTIVE           = new Shader("default_shaders/refractive.glsl");

    private int shaderProgram;
    private List<InternalShaderCombined> internalShaders = new ArrayList<>();
    private Map<String, Integer> uniformLocations = new HashMap<>();

    public Shader(String path)
    {
        String fullSource = IOUtils.readResource(path, IOUtils::resourceToString);
        assert fullSource != null : "Shader#Shader(String)#fullSource should never be null!";
        String[] splitSource = fullSource.split(SPLIT_TOKEN);
        for (int string = 1; string < splitSource.length; ++string)
        {
            String type = splitSource[string].substring(0, TYPE_SPECIFIER_LENGTH);
            String source = splitSource[string].substring(TYPE_SPECIFIER_LENGTH);
            switch (type)
            {
                case "vert":
                    internalShaders.add(new InternalShaderCombined(GL_VERTEX_SHADER, source));
                    break;
                case "frag":
                    internalShaders.add(new InternalShaderCombined(GL_FRAGMENT_SHADER, source));
                    break;
                case "geom":
                    internalShaders.add(new InternalShaderCombined(GL_GEOMETRY_SHADER, source));
                    break;
                default:
                    MAIN_LOGGER.error("Unknown shader type " + type + "!");
                    break;
            }
        }
        compile();
    }

    private int createProgram(@NotNull int... shaders)
    {
        int shaderProgram = glCreateProgram();
        for (int shader : shaders)
        {
            glAttachShader(shaderProgram, shader);
        }
        glLinkProgram(shaderProgram);
        glValidateProgram(shaderProgram);
        if (EntryPoint.DEBUG)
        {
            try (MemoryStack stack = MemoryStack.stackPush())
            {
                IntBuffer linkStatus = stack.mallocInt(1);
                IntBuffer validateStatus = stack.mallocInt(1);
                glGetProgramiv(shaderProgram, GL_LINK_STATUS, linkStatus);
                glGetProgramiv(shaderProgram, GL_VALIDATE_STATUS, validateStatus);
                if (linkStatus.get() == GL_FALSE || validateStatus.get() == GL_FALSE)
                {
                    MAIN_LOGGER.info(String.format("Program %d Info Log: %s", shaderProgram, glGetProgramInfoLog(shaderProgram)));
                }
            }
        }
        for (int shader : shaders)
        {
            glDeleteShader(shader);
        }
        return shaderProgram;
    }

    private void compile()
    {
        internalShaders.forEach(InternalShaderCombined::compile);
        List<Integer> internalShaderIds = internalShaders.stream().map(InternalShaderCombined::getShaderId).collect(Collectors.toList());
        shaderProgram = createProgram(ArrayUtils.toPrimitiveArrayI(internalShaderIds));
    }

    public void setInteger(int value, String name)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(shaderProgram, name));
        }
        glUniform1i(uniformLocations.get(name), value);
    }

    public void setFloat(float value, String name)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(shaderProgram, name));
        }
        glUniform1f(uniformLocations.get(name), value);
    }

    public void setMatrix4f(Matrix4f matrix, String name)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(shaderProgram, name));
        }
        glUniformMatrix4fv(uniformLocations.get(name), false, matrix.matrix);
    }

    public void setVector3f(Vector3f vector3f, String name)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(shaderProgram, name));
        }
        glUniform3f(uniformLocations.get(name), vector3f.getX(), vector3f.getY(), vector3f.getZ());
    }

    public void setVector4f(Vector4f vector4f, String name)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(shaderProgram, name));
        }
        glUniform4f(uniformLocations.get(name), vector4f.getX(), vector4f.getY(), vector4f.getZ(), vector4f.getW());
    }

    public void bind()
    {
        glUseProgram(shaderProgram);
    }

    public void unbind()
    {
        glUseProgram(0);
    }

    public void recompile()
    {
        delete();
        compile();
    }

    public void delete()
    {
        unbind();
        glDeleteProgram(shaderProgram);
        internalShaders.clear();
        uniformLocations.clear();
    }

    private static class InternalShaderCombined
    {
        private String source;
        private int shaderType, shaderId;

        private InternalShaderCombined(int shaderType, String source)
        {
            this.source = source;
            this.shaderType = shaderType;
        }

        private void compile()
        {
            shaderId = createShader(shaderType, source);
        }

        private static int createShader(int shaderType, String source)
        {
            int shader = glCreateShader(shaderType);
            glShaderSource(shader, source);
            glCompileShader(shader);
            if (EntryPoint.DEBUG)
            {
                try (MemoryStack stack = MemoryStack.stackPush())
                {
                    IntBuffer compileStatus = stack.mallocInt(1);
                    glGetShaderiv(shader, GL_COMPILE_STATUS, compileStatus);
                    if (compileStatus.get() == GL_FALSE)
                    {
                        MAIN_LOGGER.error(String.format("Shader %d Info Log: %s", shader, glGetShaderInfoLog(shader)));
                    }
                }
            }
            return shader;
        }

        private int getShaderId()
        {
            return shaderId;
        }
    }
}