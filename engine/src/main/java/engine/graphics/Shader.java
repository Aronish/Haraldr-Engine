package engine.graphics;

import engine.debug.Logger;
import engine.main.ArrayUtils;
import engine.main.EntryPoint;
import engine.main.IOUtils;
import engine.math.Matrix4f;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.math.Vector4f;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import static org.lwjgl.opengl.GL20.glUniform2f;
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
    private String path;
    private int programHandle;
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

    public static Shader createShaderWithSwitches(String path, List<String> switches)
    {
        StringBuilder key = new StringBuilder(path);
        for (String define : switches) key.append(define);
        return Shader.createFromSource(key.toString(), IOUtils.readResource(path, IOUtils::resourceToString), switches);
    }

    private static Shader createFromSource(String uniqueKey, String source, List<String> switches)
    {
        if (ResourceManager.isShaderLoaded(uniqueKey))
        {
            return ResourceManager.getLoadedShader(uniqueKey);
        }
        else
        {
            List<InternalShader> internalShaders = ShaderParser.parseShaderSource(source, switches);
            internalShaders.forEach(InternalShader::compile);
            Shader shader = new Shader(internalShaders);
            ResourceManager.addShader(uniqueKey, shader);
            return shader;
        }
    }

    private Shader(String path)
    {
        this.path = path;
        compile();
        if (EntryPoint.DEBUG) Logger.info("Compiled shader " + path);
    }

    private Shader(@NotNull List<InternalShader> internalShaders)
    {
        this.internalShaders = internalShaders;
        List<Integer> internalShaderIds = internalShaders.stream().map(InternalShader::getShaderId).collect(Collectors.toList());
        programHandle = createProgram(ArrayUtils.toPrimitiveArrayI(internalShaderIds));
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
                    Logger.error(String.format("Program %d Info Log: %s", shaderProgram, glGetProgramInfoLog(shaderProgram)));
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
        programHandle = createProgram(ArrayUtils.toPrimitiveArrayI(internalShaderIds));
    }

    public void setBoolean(String name, boolean value)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(programHandle, name));
        }
        glUniform1f(uniformLocations.get(name), value ? 1f : 0f);
    }

    public void setInteger(String name, int value)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(programHandle, name));
        }
        glUniform1i(uniformLocations.get(name), value);
    }

    public void setFloat(String name, float value)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(programHandle, name));
        }
        glUniform1f(uniformLocations.get(name), value);
    }

    public void setVector2f(String name, Vector2f vector)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(programHandle, name));
        }
        glUniform2f(uniformLocations.get(name), vector.getX(), vector.getY());
    }

    public void setVector3f(String name, Vector3f vector)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(programHandle, name));
        }
        glUniform3f(uniformLocations.get(name), vector.getX(), vector.getY(), vector.getZ());
    }

    public void setVector4f(String name, Vector4f vector)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(programHandle, name));
        }
        glUniform4f(uniformLocations.get(name), vector.getX(), vector.getY(), vector.getZ(), vector.getW());
    }

    public void setMatrix4f(String name, Matrix4f matrix)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(programHandle, name));
        }
        glUniformMatrix4fv(uniformLocations.get(name), false, matrix.matrix);
    }

    public void bind()
    {
        glUseProgram(programHandle);
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
        glDeleteProgram(programHandle);
        internalShaders.clear();
        uniformLocations.clear();
    }

    public int getProgramHandle()
    {
        return programHandle;
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
                        Logger.error(String.format("Shader %d Info Log: %s", shader, glGetShaderInfoLog(shader)));
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