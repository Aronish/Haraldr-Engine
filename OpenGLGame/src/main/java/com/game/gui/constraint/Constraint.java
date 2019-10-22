package com.game.gui.constraint;

import com.game.math.Vector3f;

//Defines how width, height, position, etc. is represented as vertex data.
public abstract class Constraint
{
    public abstract float[] createVertexData(int width, int height, int windowWidth, int windowHeight);
}
