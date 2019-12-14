package graphics;

import math.Vector4f;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.glDeleteProgram;
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
public class Shader
{
    public static final Shader SHADER = new Shader("shaders/shader");
    public static final Shader INSTANCED_SHADER = new Shader("shaders/instanced_shader.vert", "shaders/shader.frag");
    public static final Shader FLAT_COLOR_SHADER = new Shader("shaders/flat_shader");
    public static final Shader MULTI_DRAW_SHADER = new Shader("shaders/indirect_shader");
    public static final Shader COMBINED_SHADER = new Shader("shaders/combined");

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

    Shader(String vertexShaderPath, String fragmentShaderPath)
    {
        int vertexShader = createShader(GL_VERTEX_SHADER, readShaderFile(vertexShaderPath));
        int fragmentShader = createShader(GL_FRAGMENT_SHADER, readShaderFile(fragmentShaderPath));

        glCompileShader(vertexShader);
        //System.out.println(glGetShaderInfoLog(vertexShader));
        glCompileShader(fragmentShader);
        //System.out.println(glGetShaderInfoLog(fragmentShader));

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glValidateProgram(shaderProgram);
        //System.out.println(glGetProgramInfoLog(shaderProgram));
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
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
        return shader;
    }

    /**
     * Reads a shader file and creates a string with the source code.
     * @param path the path of the file to be read.
     * @return the source code string.
     */
    @NotNull
    private String readShaderFile(String path)
    {
        StringBuilder sb = new StringBuilder();
        try (InputStream file = Shader.class.getModule().getResourceAsStream(path))
        {
            if (file == null)
            {
                throw new NullPointerException("Shader file not found!");
            }
            int data = file.read();
            while (data != -1)
            {
                sb.append((char) data);
                data = file.read();
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Sets data of a uniform mat4 with the specified name. Caches the location.
     * @param matrix the matrix data.
     * @param name the name of the mat4 uniform.
     */
    public void setMatrix(float[] matrix, String name)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(shaderProgram, name));
        }
        glUniformMatrix4fv(uniformLocations.get(name), false, matrix);
    }

    public void setVector4f(Vector4f vector4f, String name)
    {
        if (!uniformLocations.containsKey(name))
        {
            uniformLocations.put(name, glGetUniformLocation(shaderProgram, name));
        }
        glUniform4f(uniformLocations.get(name), vector4f.getX(), vector4f.getY(), vector4f.getZ(), vector4f.getW());
    }

    public void use()
    {
        glUseProgram(shaderProgram);
    }

    private void unuse()
    {
        glUseProgram(0);
    }

    private void delete()
    {
        unuse();
        glDeleteProgram(shaderProgram);
    }

    public static void dispose()
    {
        SHADER.delete();
        INSTANCED_SHADER.delete();
        FLAT_COLOR_SHADER.delete();
    }
}