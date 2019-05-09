package main.java.debug;

import main.java.graphics.Shader;
import main.java.graphics.VertexArray;
import main.java.math.Matrix4f;
import main.java.math.Vector3f;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

/**
 * A simple line, mostly used for debugging.
 */
public class Line {

    private Vector3f otherVertex;
    private VertexArray vertexArray;
    private Shader shader;
    private Matrix4f matrix;
    private int matrixLocation;

    /**
     * Constructs the line. Now only able to be bound to a TexturedModel inside an Entity.
     * @param position the position of the line, i.e. the first vertex.
     * @param relativePosition a relative position to adjust the position. Mainly if it's in the center of a TexturedModel that has an offset.
     * @param entityPosition the position of the whole entity in the world.
     * @param otherVertex the other vertex to draw the line to.
     */
    public Line(Vector3f position, Vector3f relativePosition, Vector3f entityPosition, Vector3f otherVertex){
        Vector3f actualPosition = position.add(entityPosition).add(relativePosition);
        this.vertexArray = new VertexArray(new float[] {actualPosition.x, actualPosition.y, 0.0f, 0.0f});
        setOtherVertex(otherVertex);
        setShader();
        setMatrixLocation();
        updateMatrix();
    }

    /**
     * Sets the other vertex to draw the line to. Can be any vertex.
     * @param otherVertex the other vertex.
     */
    public void setOtherVertex(Vector3f otherVertex){
        this.otherVertex = otherVertex;
        updateVertexData();
    }

    /**
     * Updates the already loaded vertex buffer for the line with glBufferSubData.
     */
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

    /**
     * Sets the shader of this line.
     */
    private void setShader(){
        this.shader = new Shader("src/main/java/shaders/line_shader");
    }

    /**
     * Gets the shader of this line.
     * @return the shader.
     */
    public Shader getShader(){
        return this.shader;
    }

    /**
     * Gets the vertex array of this line.
     * @return the vertex array.
     */
    public VertexArray getVertexArray(){
        return this.vertexArray;
    }
}
