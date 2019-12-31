package engine.graphics;

import engine.main.EntryPoint;
import engine.math.Matrix4f;
import engine.math.Vector4f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
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
@SuppressWarnings("WeakerAccess")
public class Shader
{
    public static final Shader DEFAULT = new Shader("default_shaders/default");

    private String vertexShaderPath, fragmentShaderPath;
    private int shaderProgram;
    private Map<String, Integer> uniformLocations = new HashMap<>();

    /**
     * Constructor with a parameter for the general shader file path.
     * Both kinds of shaders must have the same name as the extensions are added afterwards for simplicity.
     * @param generalShaderPath the general shader file path, with no extension.
     */
    public Shader(String generalShaderPath)
    {
        this(generalShaderPath + ".vert", generalShaderPath + ".frag");
    }

    public Shader(String vertexShaderPath, String fragmentShaderPath)
    {
        this.vertexShaderPath = vertexShaderPath;
        this.fragmentShaderPath = fragmentShaderPath;
        compile();
    }

    /**
     * Creates a shader object for the specified shader type with the specified source code.
     * @param shaderType the shader type to create (GL_VERTEX_SHADER or GL_FRAGMENT_SHADER).
     * @param source the source code in a single string.
     * @return the shader object ID.
     */
    private int createShader(int shaderType, String source)
    {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, source);
        glCompileShader(shader);
        if (EntryPoint.DEBUG) System.out.println(glGetShaderInfoLog(shader));
        return shader;
    }

    private int createProgram(int vertexShader, int fragmentShader)
    {
        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glValidateProgram(shaderProgram);
        if (EntryPoint.DEBUG) System.out.println(glGetProgramInfoLog(shaderProgram));
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        return shaderProgram;
    }

    private void compile()
    {
        int vertexShader = createShader(GL_VERTEX_SHADER, readShaderFile(vertexShaderPath));
        int fragmentShader = createShader(GL_FRAGMENT_SHADER, readShaderFile(fragmentShaderPath));
        shaderProgram = createProgram(vertexShader, fragmentShader);
    }

    /**
     * Finds appropriate module to read from and reads the shader file.
     * @param path the file path of the shader file.
     * @return the source code of the shader in one string. Might be null.
     */
    @Nullable
    private String readShaderFile(String path)
    {
        try (InputStream inputStream = Shader.class.getModule().getResourceAsStream(path))
        {
            if (inputStream == null)
            {
                try (InputStream inputStreamClient = EntryPoint.application.getClass().getModule().getResourceAsStream(path))
                {
                    if (inputStreamClient == null)
                    {
                        throw new NullPointerException("Shader file not found!");
                    }
                    else
                    {
                        return readToStringBuilder(inputStreamClient);
                    }
                }
            }
            else
            {
                return readToStringBuilder(inputStream);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @NotNull
    private String readToStringBuilder(@NotNull InputStream file)
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

    /**
     * Sets data of a uniform mat4 with the specified name. Caches the location.
     * @param matrix the matrix data.
     * @param name the name of the mat4 uniform.
     */
    public void setMatrix4f(Matrix4f matrix, String name)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(shaderProgram, name));
        }
        glUniformMatrix4fv(uniformLocations.get(name), false, matrix.matrix);
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
        uniformLocations.clear();
    }
}