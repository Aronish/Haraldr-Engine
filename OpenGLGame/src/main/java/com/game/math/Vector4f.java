package com.game.math;

import org.jetbrains.annotations.NotNull;

import static com.game.Application.MAIN_LOGGER;

public class Vector4f
{
    private float x, y, z, w;

    public Vector4f() {}

    public Vector4f(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void set(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void set(@NotNull Vector4f other)
    {
        x = other.x;
        y = other.y;
        z = other.z;
        w = other.w;
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public float getZ()
    {
        return z;
    }

    public float getW()
    {
        return w;
    }

    public void printVector()
    {
        MAIN_LOGGER.info("X: " + x + " Y: " + y + " Z: " + z);
    }
}
