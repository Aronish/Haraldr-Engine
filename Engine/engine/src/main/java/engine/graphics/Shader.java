package engine.graphics;

import engine.main.ArrayUtils;
import engine.main.EntryPoint;
import engine.main.IOUtils;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.math.Vector4f;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static engine.main.Application.MAIN_LOGGER;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
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

/**
 * Loads a shader program with a vertex and fragment shader.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Shader
{
    public static final Shader DEFAULT2D = new Shader("default_shaders/default2D.vert", "default_shaders/default2D.frag");
    public static final Shader DIFFUSE = new Shader("default_shaders/diffuse.vert", "default_shaders/diffuse.frag");
    public static final Shader VISIBLE_NORMALS = new Shader("default_shaders/visibleNormals.vert", "default_shaders/visibleNormals.geom", "default_shaders/visibleNormals.frag");

    private int shaderProgram;
    private List<InternalShader> internalShaders = new ArrayList<>();
    private Map<String, Integer> uniformLocations = new HashMap<>();

    public Shader(String vertexShaderPath, String fragmentShaderPath)
    {
        internalShaders.add(new InternalShader(GL_VERTEX_SHADER, vertexShaderPath));
        internalShaders.add(new InternalShader(GL_FRAGMENT_SHADER, fragmentShaderPath));
        compile();
    }

    public Shader(String vertexShaderPath, String geometryShaderPath, String fragmentShaderPath)
    {
        internalShaders.add(new InternalShader(GL_VERTEX_SHADER, vertexShaderPath));
        internalShaders.add(new InternalShader(GL_GEOMETRY_SHADER, geometryShaderPath));
        internalShaders.add(new InternalShader(GL_FRAGMENT_SHADER, fragmentShaderPath));
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
        if (EntryPoint.DEBUG) MAIN_LOGGER.info(glGetProgramInfoLog(shaderProgram));
        for (int shader : shaders)
        {
            glDeleteShader(shader);
        }
        return shaderProgram;
    }

    private void compile()
    {
        internalShaders.forEach(InternalShader::compile);
        List<Integer> internalShaderIds = internalShaders.stream().map(InternalShader::getShaderId).collect(Collectors.toList());
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

    private static class InternalShader
    {
        private String sourcePath;
        private int shaderType, shaderId;

        private InternalShader(int shaderType, String sourcePath)
        {
            this.sourcePath = sourcePath;
            this.shaderType = shaderType;
        }

        private void compile()
        {
            shaderId = createShader(shaderType, IOUtils.readResource(sourcePath, InternalShader::readSourceToString));
        }

        private static int createShader(int shaderType, String source)
        {
            int shader = glCreateShader(shaderType);
            glShaderSource(shader, source);
            glCompileShader(shader);

            if (EntryPoint.DEBUG) System.out.println(glGetShaderInfoLog(shader));
            return shader;
        }

        private int getShaderId()
        {
            return shaderId;
        }

        @NotNull
        private static String readSourceToString(@NotNull InputStream file)
        {
            StringBuilder stringBuilder = new StringBuilder();
            try
            {
                int data = file.read();
                while (data != -1)
                {
                    stringBuilder.append((char) data);
                    data = file.read();
                }
            }catch (IOException e)
            {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }
    }
}