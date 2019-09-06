package com.game.gui;

import com.game.math.Matrix4f;
import com.game.math.Vector2f;
import com.game.math.Vector3f;

public abstract class GUIComponent
{
    private Vector3f position;
    private Vector2f scale = new Vector2f();
    private Matrix4f modelMatrix;

    public GUIComponent(Vector3f position, float scale)
    {
        this.position = position;
        this.scale.setBoth(scale);
        calculateMatrix();
    }

    public void setScale(float scale)
    {
        this.scale.setBoth(scale);
        calculateMatrix();
    }

    public void calculateMatrix()
    {
        modelMatrix = Matrix4f.transformPixelSpace(position, scale);
    }

    public float[] getMatrixArray()
    {
        return modelMatrix.matrix;
    }
}
