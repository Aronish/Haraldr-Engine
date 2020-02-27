package engine.math;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static engine.main.Application.MAIN_LOGGER;

@SuppressWarnings("unused")
public class Vector3f
{
    private float x, y, z;

    public Vector3f() {}

    public Vector3f(float all)
    {
        x = all;
        y = all;
        z = all;
    }

    public Vector3f(float x, float y)
    {
        this(x, y, 0.0f);
    }

    public Vector3f(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(double x, double y, double z)
    {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }

    public void set(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(@NotNull Vector3f other)
    {
        x = other.x;
        y = other.y;
    }

    public void setX(float x)
    {
        this.x = x;
    }

    public void setY(float y)
    {
        this.y = y;
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

    @NotNull
    @Contract("_, _ -> new")
    public static Vector3f add(@NotNull Vector3f first, @NotNull Vector3f second)
    {
        return new Vector3f(first.x + second.x, first.y + second.y, first.z + second.z);
    }

    public void add(@NotNull Vector3f other)
    {
        x += other.x;
        y += other.y;
        z += other.z;
    }

    @NotNull
    @Contract("_, _ -> new")
    public static Vector3f subtract(@NotNull Vector3f first, @NotNull Vector3f second)
    {
        return new Vector3f(first.x - second.x, first.y - second.y, first.z - second.z);
    }

    /**
     * Subtracts a value from the y component.
     * @param dy the value to subtract.
     * @return the resulting vector.
     */
    public Vector3f subtractY(float dy)
    {
        return new Vector3f(x, y - dy);
    }

    public Vector3f multiply(float scalar)
    {
        return new Vector3f(x * scalar, y * scalar, z * scalar);
    }

    public float length()
    {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    @Contract(pure = true)
    public static float slope(@NotNull Vector3f first, @NotNull Vector3f second)
    {
        return (second.y - first.y) / (second.x - first.x);
    }

    public void normalize()
    {
        x /= length();
        y /= length();
        z /= length();
    }

    @NotNull
    @Contract("_ -> new")
    public static Vector3f normalize(@NotNull Vector3f vector)
    {
        return new Vector3f(vector.x / vector.length(), vector.y / vector.length(), vector.z / vector.length());
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static Vector3f cross(@NotNull Vector3f a, @NotNull Vector3f b)
    {
        return new Vector3f(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
    }

    public void print()
    {
        MAIN_LOGGER.info("X: " + x + " Y: " + y + " Z: " + z);
    }
}
