package main.java.graphics;

import main.java.math.Matrix4f;
import main.java.math.Vector3f;

import static org.lwjgl.opengl.GL46.*;

public class TexturedModel{

    private Shader shader;
    private VertexArray vertexArray;
    private Texture texture;
    private int matrixLocation;

    protected Matrix4f matrix;

    protected void setVertexArray(VertexArray array){
        this.vertexArray = array;
    }

    protected void setShader(String shaderPath){
        this.shader = new Shader(shaderPath);
    }

    protected void setTexture(String filePath){
        this.texture = new Texture(filePath);
    }

    protected void setMatrixLocation(boolean isStaticModel){
        this.matrixLocation = glGetUniformLocation(this.shader.getShaderProgram(), (isStaticModel ? "MP" : "MVP"));
    }

    //Redundant in class definition if used in main update loop.
    public void updateMatrix(Vector3f position, float rotation, float scale){
        this.matrix = new Matrix4f().MVP(position, rotation, scale);
    }

    private void setUniformMatrix(){
        glUniformMatrix4fv(this.matrixLocation, false, this.matrix.matrix);
    }

    public void render(){
        this.shader.use();
        this.setUniformMatrix();
        this.vertexArray.bind();
        this.texture.bind();
        this.vertexArray.draw();
        this.texture.unbind();
        this.vertexArray.unbind();
        this.shader.unuse();
    }
}
