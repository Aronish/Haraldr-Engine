package com.game.gui;

import com.game.Window;
import com.game.event.WindowResizedEvent;
import com.game.graphics.VertexArray;
import com.game.graphics.VertexBuffer;
import com.game.gui.constraint.Constraint;
import com.game.math.Matrix4f;
import com.game.math.Vector2f;
import com.game.math.Vector3f;

public abstract class GUIComponent
{
    protected Vector3f position;
    private Matrix4f modelMatrix;
    protected VertexArray vertexArray;
    private int windowWidth, windowHeight;

    protected Constraint constraint;

    public GUIComponent(Vector3f position, Constraint constraint, Window window)
    {
        this.position = position;
        vertexArray = new VertexArray();
        this.constraint = constraint;
        windowWidth = window.getWidth();
        windowHeight = window.getHeight();
        calculateMatrix();
    }

    private void calculateMatrix()
    {
        modelMatrix = Matrix4f.transform(position, 0.0f, new Vector2f(1.0f), false);
    }

    protected float[] createVertexData(int width, int height, int padding)
    {
        return constraint.createVertexData(width, height, windowWidth, windowHeight, padding);
    }

    public void onResize(WindowResizedEvent windowResizedEvent)
    {
        windowWidth = windowResizedEvent.width;
        windowHeight = windowResizedEvent.height;
    }

    protected void setPositionX(float x)
    {
        position.setX(x);
        calculateMatrix();
    }

    protected void setPositionY(float y)
    {
        position.setY(y);
        calculateMatrix();
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
