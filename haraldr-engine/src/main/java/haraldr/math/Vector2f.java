package haraldr.math;

import haraldr.debug.Logger;
import jsonparser.JSONArray;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Vector2f
{
    public static final Vector2f IDENTITY = new Vector2f();

    private float x, y;

    public Vector2f() {}

    public Vector2f(float all)
    {
        x = all;
        y = all;
    }

    public Vector2f(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2f(double x, double y)
    {
        this.x = (float) x;
        this.y = (float) y;
    }

    public Vector2f(@NotNull JSONArray jsonArray)
    {
        x = (float) jsonArray.getDouble(0);
        y = (float) jsonArray.getDouble(1);
    }

    public Vector2f set(float x, float y)
    {
        this.x = x;
        this.y = y;
        return this;
    }

    public void set(double x, double y)
    {
        this.x = (float) x;
        this.y = (float) y;
    }

    public Vector2f set(float all)
    {
        x = all;
        y = all;
        return this;
    }

    public Vector2f set(@NotNull Vector2f other)
    {
        x = other.x;
        y = other.y;
        return this;
    }

    public void setX(float x)
    {
        this.x = x;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public void add(float all)
    {
        x += all;
        y += all;
    }

    public void add(double all)
    {
        x += all;
        y += all;
    }

    public void add(float x, float y)
    {
        this.x += x;
        this.y += y;
    }

    public void add(@NotNull Vector2f other)
    {
        x += other.getX();
        y += other.getY();
    }

    public Vector2f addX(float dx)
    {
        x += dx;
        return this;
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

    public void multiply(float scalar)
    {
        x *= scalar;
        y *= scalar;
    }

    public void normalize()
    {
        double length = length();
        x /= length;
        y /= length;
    }

    public double length()
    {
        return (float) Math.sqrt(x * x + y * y);
    }

    /////STATIC OPERATORS (These Don't modify this) /////////////////////////////

    @Contract("_, _ -> new")
    public static @NotNull Vector2f add(@NotNull Vector2f first, @NotNull Vector2f second)
    {
        return new Vector2f(first.x + second.x, first.y + second.y);
    }

    @Contract("_, _ -> new")
    public static @NotNull Vector2f add(@NotNull Vector2f vector, float scalar)
    {
        return new Vector2f(vector.x + scalar, vector.y + scalar);
    }

    @Contract("_, _ -> new")
    public static @NotNull Vector2f subtract(@NotNull Vector2f first, @NotNull Vector2f second)
    {
        return new Vector2f(first.x - second.x, first.y - second.y);
    }

    @Contract("_, _ -> new")
    public static @NotNull Vector2f multiply(@NotNull Vector2f vector, float scalar)
    {
        return new Vector2f(vector.x * scalar, vector.y * scalar);
    }

    @Contract("_, _ -> new")
    public static @NotNull Vector2f multiply(@NotNull Vector2f left, Vector2f right)
    {
        return new Vector2f(left.x * right.x, left.y * right.y);
    }

    @Contract("_ -> new")
    public static @NotNull Vector2f negate(@NotNull Vector2f vector)
    {
        return new Vector2f(-vector.x, -vector.y);
    }

    @Contract(pure = true)
    public static float slope(@NotNull Vector2f first, @NotNull Vector2f second)
    {
        return (second.y - first.y) / (second.x - first.x);
    }

    @Contract("_ -> new")
    public static @NotNull Vector2f normalize(@NotNull Vector2f vector)
    {
        return new Vector2f(vector.x / vector.length(), vector.y / vector.length());
    }

    @Contract(pure = true)
    public static float dot(@NotNull Vector2f first, @NotNull Vector2f second)
    {
        return first.x * second.x + first.y * second.y;
    }

    public void print()
    {
        Logger.info("X: " + x + " Y: " + y);
    }
}
