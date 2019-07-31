package com.game.graphics;

import com.game.physics.AABB;

/**
 * Class for handling models for game objects. Contains a VertexArray and an AABB.
 */
public class Model {

    private VertexArray vertexArray;
    private AABB aabb;

    /**
     * Constructor for setting only custom texture coordinates.
     * @param textureCoordinates the texture coordinates.
     */
    Model(float[] textureCoordinates){
        setVertexArray(textureCoordinates);
        aabb = new AABB();
    }

    /**
     * Constructor with custom vertices and texture coordinates.
     * @param vertices the vertices of the model.
     * @param texcoords the texture coordinates of the model.
     * @param width the width of this Model (AABB).
     * @param height the height of this Model (AABB).
     */
    Model(float[] vertices, float[] texcoords, float width, float height){
        setVertexArray(vertices, texcoords);
        aabb = new AABB(width, height);
    }

    /**
     * Instantiates the VertexArray of this Model with the provided texture coordinates.
     * @param textureCoordinates the texture coordinates.
     */
    private void setVertexArray(float[] textureCoordinates){
        vertexArray = new VertexArray(textureCoordinates);
    }

    /**
     * Instantiates the VertexArray of this Model with the provided vertices and texture coordinates.
     * @param vertices the vertices.
     * @param textureCoordinates the texture coordinates.
     */
    private void setVertexArray(float[] vertices, float[] textureCoordinates){
        vertexArray = new VertexArray(vertices, textureCoordinates);
    }

    /**
     * @return the VertexArray.
     */
    VertexArray getVertexArray(){
        return vertexArray;
    }

    /**
     * @return the AABB.
     */
    public AABB getAABB(){
        return aabb;
    }

    /**
     * Deletes the buffer objects.
     */
    void cleanUp(){
        vertexArray.unbind();
        vertexArray.delete();
    }
}
