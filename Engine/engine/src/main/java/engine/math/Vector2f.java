package engine.math;

import org.jetbrains.annotations.NotNull;

import static engine.main.Application.MAIN_LOGGER;

/**
 * A simple class to represent a 2D vector of floats.
 */
public class Vector2f
{
    private float x, y;

    public Vector2f() {}

    public Vector2f(float both)
    {
        x = both;
        y = both;
    }

    public Vector2f(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2f setBoth(float both)
    {
        x = both;
        y = both;
        return this;
    }

    public void reset()
    {
        x = 0.0f;
        y = 0.0f;
    }

    public void add(@NotNull Vector2f other)
    {
        x += other.getX();
        y += other.getY();
    }

    public void addX(float dx)
    {
        x += dx;
    }

    public void addY(float dy)
    {
        y += dy;
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public void printVector()
    {
        MAIN_LOGGER.info("X: " + x + " Y: " + y);
    }
}