package main.java.graphics;

import java.io.File;
import java.util.Scanner;

import static org.lwjgl.opengl.GL46.*;

class Shader {

    private int shaderProgram;

    Shader(String generalShaderPath){
        this(generalShaderPath + ".vert", generalShaderPath + ".frag");
    }

    private Shader(String vertexShaderPath, String fragmentShaderPath){
        int vertexShader = createShader(GL_VERTEX_SHADER, readShaderFile(vertexShaderPath));
        int fragmentShader = createShader(GL_FRAGMENT_SHADER, readShaderFile(fragmentShaderPath));

        glCompileShader(vertexShader);
        System.out.println(glGetShaderInfoLog(vertexShader));
        glCompileShader(fragmentShader);
        System.out.println(glGetShaderInfoLog(fragmentShader));

        this.shaderProgram = glCreateProgram();
        glAttachShader(this.shaderProgram, vertexShader);
        glAttachShader(this.shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glValidateProgram(shaderProgram);
        System.out.println(glGetProgramInfoLog(shaderProgram));
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    private int createShader(int shaderType, String source){
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, source);
        return shader;
    }

    int getShaderProgram(){
        return shaderProgram;
    }

    private String readShaderFile(String filePath){
        File shaderFile = null;
        StringBuilder stringBuilder;
        try {
            shaderFile = new File(filePath);
        }catch (Exception e){
            System.out.println("Error 1");
        }
        stringBuilder = new StringBuilder((int) shaderFile.length());
        try (Scanner scanner = new Scanner(shaderFile)){
            while (scanner.hasNextLine()){
                stringBuilder.append(scanner.nextLine() + System.lineSeparator());
            }
        }catch(Exception e){
            System.out.println("Error 2");
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    void use(){
        glUseProgram(shaderProgram);
    }

    void unuse(){
        glUseProgram(0);
    }
}
