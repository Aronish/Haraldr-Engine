package com.game.graphics;

import com.game.physics.AABB;

/**
 * Class for handling models for game objects. Contains a VertexArray and an AABB.
 */
public class Model {

    private VertexArray vertexArray;
    private AABB aabb = new AABB();

    Model()
    {
        vertexArray = new VertexArray();
    }

    Model(float[] textureCoordinates){
        setVertexArray(textureCoordinates);
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

    private void setVertexArray(float[] textureCoordinates){
        vertexArray = new VertexArray(textureCoordinates);
    }

    private void setVertexArray(float[] vertices, float[] textureCoordinates){
        vertexArray = new VertexArray(vertices, textureCoordinates);
    }

    VertexArray getVertexArray(){
        return vertexArray;
    }

    public AABB getAABB(){
        return aabb;
    }

    void cleanUp(){
        vertexArray.unbind();
        vertexArray.delete();
    }
}
