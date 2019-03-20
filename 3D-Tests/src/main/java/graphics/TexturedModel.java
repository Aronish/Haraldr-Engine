package main.java.graphics;

import main.java.math.Matrix4f;
import main.java.math.Vector3f;

import static org.lwjgl.opengl.GL46.*;

public class TexturedModel{

    private Shader shader;
    private VertexArray vertexArray;
    private Texture texture;
    private int matrixLocation;

    protected Vector3f position;
    protected float rotation;
    protected float scale;
    protected Matrix4f matrix;

    public TexturedModel(){
        this(new Vector3f(), 0.0f, 1.0f);
    }

    public TexturedModel(Vector3f position, float rotation, float scale){
        setPosition(position);
        setRotation(rotation);
        setScale(scale);
        updateMatrix();
    }

    public void setPosition(Vector3f position){
        this.position = position;
        updateMatrix();
    }

    public void setRotation(float rotation){
        this.rotation = rotation;
        updateMatrix();
    }

    public void setScale(float scale){
        this.scale = scale;
        updateMatrix();
    }

    public void setAttributes(Vector3f position, float rotation, float scale){
        setPosition(position);
        setRotation(rotation);
        setScale(scale);
        updateMatrix();
    }

    protected void updateMatrix(){
        this.matrix = new Matrix4f().MVP(this.position, this.rotation, this.scale);
    }

    protected void setVertexArray(float[] vertices, int[] indices, int[] texcoords){
        this.vertexArray = new VertexArray(vertices, indices, texcoords);
    }

    protected void setVertexArray(){
        this.vertexArray = new VertexArray();
    }

    protected void setShader(String shaderPath){
        this.shader = new Shader(shaderPath);
    }

    protected void setTexture(String filePath){
        this.texture = new Texture(filePath);
    }

    protected void setMatrixLocation(){
        this.matrixLocation = glGetUniformLocation(this.shader.getShaderProgram(), "matrix");
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
