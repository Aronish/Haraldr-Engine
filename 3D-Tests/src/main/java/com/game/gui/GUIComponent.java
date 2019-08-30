package com.game.gui;

import com.game.math.Matrix4f;
import com.game.math.Vector2f;
import com.game.math.Vector3f;

public class GUIComponent {

    private Matrix4f modelMatrix;

    public GUIComponent(Vector3f position)
    {
        modelMatrix = Matrix4f.transform(position, 0.0f, new Vector2f(0.01f), false);
    }

    public Matrix4f getMatrix()
    {
        return modelMatrix;
    }
}
