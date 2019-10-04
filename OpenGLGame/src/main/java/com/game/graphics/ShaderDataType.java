package com.game.graphics;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

public enum ShaderDataType
{
    FLOAT2  (2, GL_FLOAT, 4),
    FLOAT3  (3, GL_FLOAT, 4),
    MAT4    (4, GL_FLOAT, 4); //Need 4 of these, one per column.

    public final int size;
    public final int GLType;
    public final int typeSize;

    ShaderDataType(int size, int GLType, int typeSize)
    {
        this.size = size;
        this.GLType = GLType;
        this.typeSize = typeSize;
    }
}
