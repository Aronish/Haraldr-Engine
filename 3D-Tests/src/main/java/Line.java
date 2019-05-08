package main.java;

import main.java.graphics.Shader;
import main.java.graphics.VertexArray;
import main.java.math.Matrix4f;
import main.java.math.Vector3f;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class Line {

    private Vector3f otherVertex;
    private VertexArray vertexArray;
    private Shader shader;
    private Matrix4f matrix;
    private int matrixLocation;

    public Line(Vector3f position, Vector3f relativePosition, Vector3f entityPosition, Vector3f otherVertex){
        position.printVector();
        Vector3f actualPosition = position.add(entityPosition).add(relativePosition);
        this.vertexArray = new VertexArray(new float[] {actualPosition.x, actualPosition.y, 0.0f, 0.0f});
        setOtherVertex(otherVertex);
        setShader();
        setMatrixLocation();
        updateMatrix();
    }

    public void setOtherVertex(Vector3f otherVertex){
        this.otherVertex = otherVertex;
        updateVertexData();
    }

    private void updateVertexData(){
        this.vertexArray.updateVertexData(new float[] {this.otherVertex.x, this.otherVertex.y});
    }

    /**
     * Updates the Model-View-Projection matrix with the current attribute values.
     */
    public void updateMatrix(){
        this.matrix = new Matrix4f().MVP(new Vector3f(), 0.0f, 1.0f);
    }

    /**
     * Retrieves the matrix location in the shader for the specified TexturedModel.
     */
    public void setMatrixLocation(){
        this.matrixLocation = glGetUniformLocation(this.shader.getShaderProgram(), "matrix");
    }

    /**
     * Sets the uniform variable in the vertex shader to the current Model-View-Projection matrix.
     */
    public void setUniformMatrix(){
        glUniformMatrix4fv(this.matrixLocation, false, this.matrix.matrix);
    }

    private void setShader(){
        this.shader = new Shader("src/main/java/shaders/line_shader");
    }

    public Shader getShader(){
        return this.shader;
    }

    public VertexArray getVertexArray(){
        return this.vertexArray;
    }
}
