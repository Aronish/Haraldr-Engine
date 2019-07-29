package com.game.graphics;

import com.game.math.Vector3f;
import com.game.physics.AABB;

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

    TexturedModel(Texture spriteSheet, float[] textureCoordinates){
        setVertexArray(textureCoordinates);
        setTexture(spriteSheet);
        relativePosition = new Vector3f();
        aabb = new AABB();
    }

    /**
     * Constructor with custom vertices, indices and texture coordinates.
     * @param vertices the vertices of the model.
     * @param texcoords the texture coordinates of the model.
     * @param texturePath the file path for the texture.
     */
    TexturedModel(float[] vertices, float[] texcoords, Vector3f relativePosition, float width, float height, String texturePath){
        setVertexArray(vertices, texcoords);
        setTexture(texturePath);
        this.relativePosition = relativePosition;
        aabb = new AABB(width, height);
    }

    /**
     * Constructor with custom vertices, indices and texture coordinates.
     * @param vertices the vertices of the model.
     * @param texcoords the texture coordinates of the model.
     * //TODO
     */
    TexturedModel(float[] vertices, float[] texcoords, Vector3f relativePosition, float width, float height, Texture texture){
        setVertexArray(vertices, texcoords);
        setTexture(texture);
        this.relativePosition = relativePosition;
        aabb = new AABB(width, height);
    }

    /**
     * Sets the vertex array to the default configuration.
     */
    private void setVertexArray(){
        vertexArray = new VertexArray();
    }

    private void setVertexArray(float[] textureCoordinates){
        vertexArray = new VertexArray(textureCoordinates);
    }

    private void setVertexArray(float[] vertices, float[] textureCoordinates){
        vertexArray = new VertexArray(vertices, textureCoordinates);
    }

    /**
     * Sets a new texture object.
     * @param filePath the path of the texture file, with extension.
     */
    private void setTexture(String filePath){
        texture = new Texture(filePath, false);
    }

    private void setTexture(Texture texture){
        this.texture = texture;
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
    public VertexArray getVertexArray(){
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
