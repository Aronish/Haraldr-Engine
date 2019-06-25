package main.java.graphics;

import java.io.File;
import java.util.Scanner;

import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
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
 * Class for handling the creation and loading of shader files. Both vertex and fragment shaders.
 */
public class Shader {

    private int shaderProgram;

    /**
     * Constructor with a parameter for the general shader file path.
     * Both kinds of shaders must have the same name as the extensions are added afterwards for simplicity.
     * @param generalShaderPath the general shader file path, with no extension.
     */
    Shader(String generalShaderPath){
        this(generalShaderPath + ".vert", generalShaderPath + ".frag");
    }

    /**
     * Private constructor that takes in the different paths of the shader files. Reads the shader files, creates shaders
     * and compiles them into a shader program.
     * @param vertexShaderPath the path of the vertex shader.
     * @param fragmentShaderPath the path of the fragment shader.
     */
    private Shader(String vertexShaderPath, String fragmentShaderPath){
        int vertexShader = createShader(GL_VERTEX_SHADER, readShaderFile(vertexShaderPath));
        int fragmentShader = createShader(GL_FRAGMENT_SHADER, readShaderFile(fragmentShaderPath));

        glCompileShader(vertexShader);
        //System.out.println(glGetShaderInfoLog(vertexShader));
        glCompileShader(fragmentShader);
        //System.out.println(glGetShaderInfoLog(fragmentShader));

        this.shaderProgram = glCreateProgram();
        glAttachShader(this.shaderProgram, vertexShader);
        glAttachShader(this.shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glValidateProgram(shaderProgram);
        //System.out.println(glGetProgramInfoLog(shaderProgram));
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    /**
     * Creates a shader object for the specified shader type with the specified source code.
     * @param shaderType an integer constant (usually from OpenGl), tells OpenGL what shader type to create.
     * @param source the source code in a single string.
     * @return the shader object ID.
     */
    private int createShader(int shaderType, String source){
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, source);
        return shader;
    }

    /**
     * Reads a shader file and creates a string with the source code.
     * @param filePath the path of the file to be read.
     * @return the source code string.
     */
    private String readShaderFile(String filePath){
        File shaderFile = null;
        StringBuilder stringBuilder;
        try {
            shaderFile = new File(filePath);
        }catch (Exception e){
            System.out.println("Error 1");
        }
        if (shaderFile == null) {
            throw new IllegalStateException("Shader file was not initialized!");
        }
        stringBuilder = new StringBuilder((int) shaderFile.length());
        try (Scanner scanner = new Scanner(shaderFile)) {
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine()).append(System.lineSeparator());
            }
        } catch (Exception e) {
            System.out.println("Error 2");
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    void setMatrix(float[] matrix){
        int matrixLocation = glGetUniformLocation(this.shaderProgram, "matrix");
        glUniformMatrix4fv(matrixLocation, false, matrix);
    }

    /**
     * Uses the shader program, ready for drawing.
     */
    public void use(){
        glUseProgram(shaderProgram);
    }

    /**
     * Unuses the shader program to avoid weird conflicts.
     */
    private void unuse(){
        glUseProgram(0);
    }

    /**
     * Deletes the shader program.
     */
    void delete(){
        unuse();
        glDeleteProgram(this.shaderProgram);
    }
}
