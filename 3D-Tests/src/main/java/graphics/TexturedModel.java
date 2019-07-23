package main.java.graphics;

import main.java.math.Vector3f;
import main.java.physics.AABB;

/**
 * Class for handling models for game objects.
 */
public class TexturedModel{

    private Vector3f relativePosition;
    private VertexArray vertexArray;
    private Texture texture;
    private AABB aabb;

    /**
     * Constructor with just the paths for the shader and texture.
     * @param texturePath the file path for the texture.
     */
    TexturedModel(String texturePath){
        setVertexArray();
        setTexture(texturePath);
        relativePosition = new Vector3f();
        aabb = new AABB();
    }

    /**
     * Constructor with custom vertices, indices and texture coordinates.
     * @param vertices the vertices of the model.
     * @param indices the indices, the order to render the vertices in.
     * @param texcoords the texture coordinates of the model.
     * @param texturePath the file path for the texture.
     */
    TexturedModel(float[] vertices, int[] indices, float[] texcoords, Vector3f relativePosition, float width, float height, String texturePath){
        setVertexArray(vertices, indices, texcoords);
        setTexture(texturePath);
        this.relativePosition = relativePosition;
        aabb = new AABB(width, height);
    }

    /**
     * Sets the vertex array to the default configuration.
     */
    private void setVertexArray(){
        vertexArray = new VertexArray();
    }

    /**
     * Sets the vertex array for this object. Contains the vertices, indices and texture coordinates
     * along with their associated OpenGL buffers and pointers.
     * @param vertices an array of floats, the vertices.
     * @param indices an array of integers, the indices which tells OpenGL in what order to draw the vertices.
     * @param texcoords an array of integers, the coordinates of the texture coordinates.
     */
    private void setVertexArray(float[] vertices, int[] indices, float[] texcoords){
        vertexArray = new VertexArray(vertices, indices, texcoords);
    }

    /**
     * Sets a new texture object.
     * @param filePath the path of the texture file, with extension.
     */
    private void setTexture(String filePath){
        texture = new Texture(filePath);
    }

    /**
     * Gets the relative position of this TexturedModel (the offset inside the parent Entity).
     * @return the relative position.
     */
    public Vector3f getRelativePosition(){
        return relativePosition;
    }

    /**
     * Gets the vertex array of this object.
     * Used to retrieve information about the vertices and rarely the indices and texture coordinates.
     * @return the vertex array object.
     */
    VertexArray getVertexArray(){
        return vertexArray;
    }

    /**
     * Gets the texture.
     * @return the texture.
     */
    Texture getTexture(){
        return texture;
    }

    /**
     * Gets the bounding box of this TexturedModel.
     * @return the bounding box.
     */
    public AABB getAABB(){
        return aabb;
    }

    /**
     * Deletes the buffer objects and textures.
     */
    public void cleanUp(){
        vertexArray.delete();
        texture.delete();
    }
}
