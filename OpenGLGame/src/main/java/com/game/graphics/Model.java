package com.game.graphics;

import com.game.physics.AABB;

import static com.game.Application.MAIN_LOGGER;

/**
 * Class for handling models for game objects. Contains a VertexArray and an AABB.
 */
public class Model implements IModel
{
    private VertexArray vertexArray;
    private AABB aabb = new AABB();

    Model()
    {
        vertexArray = new VertexArray();
    }

    Model(float[] textureCoordinates)
    {
        setVertexArray(textureCoordinates);
    }

    /**
     * Constructor with custom vertices and texture coordinates.
     * @param vertices the vertices of the model.
     * @param texcoords the texture coordinates of the model.
     * @param width the width of this Model (AABB).
     * @param height the height of this Model (AABB).
     */
    Model(float[] vertices, float[] texcoords, float width, float height)
    {
        setVertexArray(vertices, texcoords);
        aabb = new AABB(width, height);
    }

    private void setVertexArray(float[] textureCoordinates)
    {
        vertexArray = new VertexArray(textureCoordinates);
    }

    private void setVertexArray(float[] vertices, float[] textureCoordinates)
    {
        vertexArray = new VertexArray(vertices, textureCoordinates);
    }

    @Override
    public VertexArray getVertexArray()
    {
        return vertexArray;
    }

    public AABB getAABB()
    {
        return aabb;
    }

    @Override
    public void dispose()
    {
        vertexArray.unbind();
        vertexArray.delete();
    }

    @Override
    public void printType() {
        MAIN_LOGGER.info("Normal Model");
    }
}
