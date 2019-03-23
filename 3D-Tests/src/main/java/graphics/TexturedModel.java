package main.java.graphics;

import main.java.math.Matrix4f;
import main.java.math.Vector3f;

import static org.lwjgl.opengl.GL46.*;

/**
 * A superclass containing common properties, setters and getters for game objects.
 * Made for easy creation of new objects.
 */
public class TexturedModel{

    private Shader shader;
    private VertexArray vertexArray;
    private Texture texture;
    private int matrixLocation;

    protected Vector3f position;
    private float rotation;
    private float scale;
    private Matrix4f matrix;

    /**
     * Default constructor if no arguments are provided.
     */
    public TexturedModel(){
        this(new Vector3f(), 0.0f, 1.0f);
    }

    /**
     * Constructor with parameters for position, rotation and scale.
     * @param position the position of the object. An origin vector. Bottom left corner.
     * @param rotation the rotation around the z-axis, in degrees.
     * @param scale the scale multiplier of this object.
     */
    public TexturedModel(Vector3f position, float rotation, float scale){
        setPosition(position);
        setRotation(rotation);
        setScale(scale);
        updateMatrix();
    }

    /**
     * Sets the position of this object.
     * @param position the position, represented with a vector from the world origin.
     */
    public void setPosition(Vector3f position){
        this.position = position;
        updateMatrix();
    }

    /**
     * Adds a vector to the position of this object.
     * @param position the vector to add to the position.
     */
    public void addPosition(Vector3f position){
        this.position.x += position.x;
        this.position.y += position.y;
        this.position.z += position.z;
        updateMatrix();
    }

    /**
     * Sets the rotation around the z-axis of this object.
     * @param rotation the rotation, in degrees.
     */
    private void setRotation(float rotation){
        this.rotation = rotation;
        updateMatrix();
    }

    /**
     * Sets the scale of this object.
     * @param scale the scale multiplier.
     */
    public void setScale(float scale){
        this.scale = scale;
        updateMatrix();
    }

    /**
     * Sets all the three attributes of this object at once.
     * @param position the position, represented with a vector from the world origin.
     * @param rotation the rotation, in degrees.
     * @param scale the scale multiplier.
     */
    public void setAttributes(Vector3f position, float rotation, float scale){
        setPosition(position);
        setRotation(rotation);
        setScale(scale);
        updateMatrix();
    }

    /**
     * Updates the Model-View-Projection matrix with the current attribute values.
     */
    protected void updateMatrix(){
        this.matrix = new Matrix4f().MVP(this.position, this.rotation, this.scale);
    }

    /**
     * Sets the vertex array for this object. Contains the vertices, indices and texture coordinates
     * along with their associated OpenGL buffers and pointers.
     * @param vertices an array of floats, the vertices.
     * @param indices an array of integers, the indices which tells OpenGL in what order to draw the vertices.
     * @param texcoords an array of integers, the coordinates of the texture coordinates.
     */
    protected void setVertexArray(float[] vertices, int[] indices, int[] texcoords){
        this.vertexArray = new VertexArray(vertices, indices, texcoords);
    }

    /**
     * Sets the vertex array to the default configuration.
     */
    protected void setVertexArray(){
        this.vertexArray = new VertexArray();
    }

    /**
     * Sets a new shader object containing a shader program with a vertex and fragment shader attached.
     * @param shaderPath the general path of the vertex and fragment shader files, without the extension.
     *                   Both files must have the same name.
     */
    protected void setShader(String shaderPath){
        this.shader = new Shader(shaderPath);
    }

    /**
     * Sets a new texture object.
     * @param filePath the path of the texture file, with extension.
     */
    protected void setTexture(String filePath){
        this.texture = new Texture(filePath);
    }

    /**
     * Gets the matrix location in the shader from OpenGL and stores the ID for later use.
     */
    protected void setMatrixLocation(){
        this.matrixLocation = glGetUniformLocation(this.shader.getShaderProgram(), "matrix");
    }

    /**
     * Sets the uniform variable in the vertex shader to the current Model-View-Projection matrix.
     */
    private void setUniformMatrix(){
        glUniformMatrix4fv(this.matrixLocation, false, this.matrix.matrix);
    }

    /**
     * Gets the position vector of this object.
     * @return the position vector.
     */
    public Vector3f getPosition(){
        return new Vector3f(this.position.x, this.position.y, this.position.z);
    }

    /**
     * Gets the vertex array of this object.
     * Used to retrieve information about the vertices and rarely the indices and texture coordinates.
     * @return the vertex array object.
     */
    private VertexArray getVertexArray(){
        return this.vertexArray;
    }

    /**
     * Gets the real width with scale compensation.
     * @return the real width.
     */
    public float getWidth(){
        return getVertexArray().getWidth() * this.scale;
    }

    /**
     * Gets the real height with scale compensation.
     * @return the real height.
     */
    public float getHeight(){
        return getVertexArray().getHeight() * this.scale;
    }

    /**
     * Main render method. Uses the shader of this object, sets the uniform matrix and bind all the needed buffers.
     * Unbinds everything after draw call is executed.
     */
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
