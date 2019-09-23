package com.game.graphics;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

public enum ShaderDataType
{
    FLOAT2(8, GL_FLOAT),
    FLOAT3(12, GL_FLOAT);
    //Matrix type will be implemented later.

    public final int size;
    public final int GLType;

    ShaderDataType(int size, int GLType)
    {
        this.size = size;
        this.GLType = GLType;
    }
}
