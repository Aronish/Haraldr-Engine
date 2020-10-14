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

    @Contract(pure = true)
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

    public Vector2f(Vector2f other)
    {
        x = other.x;
        y = other.y;
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

    public Vector2f set(double x, double y)
    {
        this.x = (float) x;
        this.y = (float) y;
        return this;
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

    public Vector2f setX(float x)
    {
        this.x = x;
        return this;
    }

    public Vector2f setY(float y)
    {
        this.y = y;
        return this;
    }

    public Vector2f add(float all)
    {
        x += all;
        y += all;
        return this;
    }

    public Vector2f add(double all)
    {
        x += all;
        y += all;
        return this;
    }

    public Vector2f add(float x, float y)
    {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2f add(@NotNull Vector2f other)
    {
        x += other.getX();
        y += other.getY();
        return this;
    }

    public Vector2f addX(float dx)
    {
        x += dx;
        return this;
    }

    public Vector2f addY(float dy)
    {
        y += dy;
        return this;
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public Vector2f multiply(float scalar)
    {
        x *= scalar;
        y *= scalar;
        return this;
    }

    public Vector2f multiply(Vector2f vector)
    {
        x *= vector.x;
        y *= vector.y;
        return this;
    }

    public Vector2f divide(float scalar)
    {
        x /= scalar;
        y /= scalar;
        return this;
    }

    public Vector2f normalize()
    {
        double length = length();
        x /= length;
        y /= length;
        return this;
    }

    public Vector2f negate()
    {
        x = -x;
        y = -y;
        return this;
    }

    public Vector2f abs()
    {
        x = Math.abs(x);
        y = Math.abs(y);
        return this;
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

    @Contract("_, _ -> new")
    public static @NotNull Vector2f divide(@NotNull Vector2f left, @NotNull Vector2f right)
    {
        return new Vector2f(left.x / right.x, left.y / right.y);
    }

    @Contract("_, _ -> new")
    public static @NotNull Vector2f divide(@NotNull Vector2f vector, float scalar)
    {
        return new Vector2f(vector.x / scalar, vector.y / scalar);
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
