package com.game.gui;

import com.game.graphics.VertexArray;
import com.game.graphics.VertexBuffer;
import com.game.math.Matrix4f;
import com.game.math.Vector2f;
import com.game.math.Vector3f;

public abstract class GUIComponent
{
    private Vector3f position;
    private Matrix4f modelMatrix;
    protected VertexArray vertexArray;

    public GUIComponent(Vector3f position)
    {
        this.position = position;
        vertexArray = new VertexArray();
        calculateMatrix();
    }

    private void calculateMatrix()
    {
        modelMatrix = Matrix4f.transform(position, 0.0f, new Vector2f(1.0f), false);
    }

    protected void setPosition(float x, float y)
    {
        position.set(x, y);
        calculateMatrix();
    }

    protected void setVertexBuffer(VertexBuffer vertexBuffer)
    {
        vertexArray.setVertexBuffer(vertexBuffer);
    }

    public float[] getMatrixArray()
    {
        return modelMatrix.matrix;
    }

    public VertexArray getVertexArray()
    {
        return vertexArray;
    }

    public abstract void draw();
}
