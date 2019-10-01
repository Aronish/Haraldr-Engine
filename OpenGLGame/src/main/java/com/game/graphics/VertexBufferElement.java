package com.game.graphics;

public class VertexBufferElement
{
    private ShaderDataType type;
    private boolean normalized;
    private int offset = 0;

    public VertexBufferElement(ShaderDataType type, boolean normalized)
    {
        this.type = type;
        this.normalized = normalized;
    }

    public void setOffset(int offset)
    {
        this.offset = offset;
    }

    public int getSize()
    {
        return type.size;
    }

    public int getTypeSize()
    {
        return type.typeSize;
    }

    public int getType()
    {
        return type.GLType;
    }

    public boolean isNormalized()
    {
        return normalized;
    }

    public int getOffset()
    {
        return offset;
    }
}
