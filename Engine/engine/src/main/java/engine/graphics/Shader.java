package engine.graphics;

import engine.main.ArrayUtils;
import engine.main.EntryPoint;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.math.Vector4f;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
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

    public static final Shader DEFAULT2D            = create("default_shaders/default2D.glsl");
    public static final Shader DIFFUSE              = create("default_shaders/diffuse.glsl");
    public static final Shader NORMAL               = create("default_shaders/normal.glsl");
    public static final Shader LIGHT_SHADER         = create("default_shaders/simple_color.glsl");
    public static final Shader VISIBLE_NORMALS      = create("default_shaders/visible_normals.glsl");
    public static final Shader REFLECTIVE           = create("default_shaders/reflective.glsl");
    public static final Shader REFRACTIVE           = create("default_shaders/refractive.glsl");
    public static final Shader PBR_TEXTURED         = create("default_shaders/pbr_textured.glsl");
    public static final Shader PBR_UNTEXTURED       = create("default_shaders/pbr_untextured.glsl");
    public static final Shader UI                   = create("default_shaders/ui.glsl");
    public static final Shader TEXT                 = create("default_shaders/text.glsl");

    private String path;
    private int shaderProgram;
    private List<InternalShader> internalShaders;
    private Map<String, Integer> uniformLocations = new HashMap<>();

    public static Shader create(String path)
    {
        if (ResourceManager.isShaderLoaded(path))
        {
            return ResourceManager.getLoadedShader(path);
        }
        else
        {
            Shader shader = new Shader(path);
            ResourceManager.addShader(path, shader);
            return shader;
        }
    }

    public static Shader createFromSource(String key, String source)
    {
        if (ResourceManager.isShaderLoaded(key))
        {
            return ResourceManager.getLoadedShader(key);
        }
        else
        {
            List<InternalShader> internalShaders = ShaderParser.parseShaderSource(source);
            internalShaders.forEach(InternalShader::compile);
            Shader shader = new Shader(internalShaders);
            ResourceManager.addShader(key, shader);
            return shader;
        }
    }

    private Shader(String path)
    {
        this.path = path;
        compile();
        if (EntryPoint.DEBUG) MAIN_LOGGER.info("Compiled shader " + path);
    }

    private Shader(@NotNull List<InternalShader> internalShaders)
    {
        this.internalShaders = internalShaders;
        List<Integer> internalShaderIds = internalShaders.stream().map(InternalShader::getShaderId).collect(Collectors.toList());
        shaderProgram = createProgram(ArrayUtils.toPrimitiveArrayI(internalShaderIds));
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
        internalShaders = ShaderParser.parseShader(path);
        internalShaders.forEach(InternalShader::compile);
        List<Integer> internalShaderIds = internalShaders.stream().map(InternalShader::getShaderId).collect(Collectors.toList());
        shaderProgram = createProgram(ArrayUtils.toPrimitiveArrayI(internalShaderIds));
    }

    public void setBoolean(String name, boolean value)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(shaderProgram, name));
        }
        glUniform1f(uniformLocations.get(name), value ? 1f : 0f);
    }

    public void setInteger(String name, int value)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(shaderProgram, name));
        }
        glUniform1i(uniformLocations.get(name), value);
    }

    public void setFloat(String name, float value)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(shaderProgram, name));
        }
        glUniform1f(uniformLocations.get(name), value);
    }

    public void setVector3f(String name, Vector3f vector)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(shaderProgram, name));
        }
        glUniform3f(uniformLocations.get(name), vector.getX(), vector.getY(), vector.getZ());
    }

    public void setVector4f(String name, Vector4f vector)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(shaderProgram, name));
        }
        glUniform4f(uniformLocations.get(name), vector.getX(), vector.getY(), vector.getZ(), vector.getW());
    }

    public void setMatrix4f(String name, Matrix4f matrix)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(shaderProgram, name));
        }
        glUniformMatrix4fv(uniformLocations.get(name), false, matrix.matrix);
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

    static class InternalShader
    {
        private String source;
        private int shaderType, shaderId;

        InternalShader(int shaderType, String source)
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