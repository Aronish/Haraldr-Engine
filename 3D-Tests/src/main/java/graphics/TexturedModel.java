package main.java.graphics;

/**
 * Class for handling models for game objects.
 */
public class TexturedModel{

    private VertexArray vertexArray;
    private Shader shader;
    private Texture texture;
    private float width, height;

    /**
     * Constructor with just the paths for the shader and texture.
     * @param shaderPath the file path for the shaders.
     * @param texturePath the file path for the texture.
     */
    TexturedModel(String shaderPath, String texturePath){
        setVertexArray();
        setShader(shaderPath);
        setTexture(texturePath);
        this.width = 1.0f;
        this.height = 1.0f;
    }

    /**
     * Constructor with custom vertices, indices and texture coordinates.
     * @param vertices the vertices of the model.
     * @param indices the indices, the order to render the vertices in.
     * @param texcoords the texture coordinates of the model.
     * @param shaderPath the file path for the shaders.
     * @param texturePath the file path for the texture.
     */
    TexturedModel(float[] vertices, int[] indices, float[] texcoords, float width, float height, String shaderPath, String texturePath){
        setVertexArray(vertices, indices, texcoords);
        setShader(shaderPath);
        setTexture(texturePath);
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the vertex array to the default configuration.
     */
    private void setVertexArray(){
        this.vertexArray = new VertexArray();
    }

    /**
     * Sets the vertex array for this object. Contains the vertices, indices and texture coordinates
     * along with their associated OpenGL buffers and pointers.
     * @param vertices an array of floats, the vertices.
     * @param indices an array of integers, the indices which tells OpenGL in what order to draw the vertices.
     * @param texcoords an array of integers, the coordinates of the texture coordinates.
     */
    private void setVertexArray(float[] vertices, int[] indices, float[] texcoords){
        this.vertexArray = new VertexArray(vertices, indices, texcoords);
    }

    /**
     * Sets a new shader object containing a shader program with a vertex and fragment shader attached.
     * @param shaderPath the general path of the vertex and fragment shader files, without the extension.
     *                   Both files must have the same name.
     */
    private void setShader(String shaderPath){
        this.shader = new Shader(shaderPath);
    }

    /**
     * Sets a new texture object.
     * @param filePath the path of the texture file, with extension.
     */
    private void setTexture(String filePath){
        this.texture = new Texture(filePath);
    }

    /**
     * Gets the vertex array of this object.
     * Used to retrieve information about the vertices and rarely the indices and texture coordinates.
     * @return the vertex array object.
     */
    VertexArray getVertexArray(){
        return this.vertexArray;
    }

    /**
     * Gets the shader.
     * @return the shader.
     */
    public Shader getShader(){
        return this.shader;
    }

    /**
     * Gets the texture.
     * @return the texture.
     */
    Texture getTexture(){
        return this.texture;
    }

    float getWidth(){
        return this.width;
    }

    float getHeight(){
        return this.height;
    }
}
