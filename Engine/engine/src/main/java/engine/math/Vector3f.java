package engine.math;

import main.JSONArray;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static engine.main.Application.MAIN_LOGGER;

@SuppressWarnings("unused")
public class Vector3f
{
    public static final Vector3f IDENTITY   = new Vector3f();
    public static final Vector3f UP         = new Vector3f(0f, 1f, 0f);
    public static final Vector3f DOWN       = new Vector3f(0f, -1f, 0f);

    private float x, y, z;

    public Vector3f() {}

    public Vector3f(float all)
    {
        x = all;
        y = all;
        z = all;
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

    public Vector3f(@NotNull JSONArray jsonArray)
    {
        x = (float) jsonArray.getDouble(0);
        y = (float) jsonArray.getDouble(1);
        z = (float) jsonArray.getDouble(2);
    }

    public void set(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(double x, double y, double z)
    {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }

    public void set(float all)
    {
        x = all;
        y = all;
        z = all;
    }

    public void set(double all)
    {
        x = (float) all;
        y = (float) all;
        z = (float) all;
    }

    public void set(@NotNull Vector3f other)
    {
        x = other.x;
        y = other.y;
        z = other.z;
    }

    public void setX(float x)
    {
        this.x = x;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public void setZ(float z)
    {
        this.z = z;
    }

    public void add(float all)
    {
        x += all;
        y += all;
        z += all;
    }

    public void add(double all)
    {
        x += all;
        y += all;
        z += all;
    }

    public void add(@NotNull Vector3f other)
    {
        x += other.x;
        y += other.y;
        z += other.z;
    }

    public void addX(float dx)
    {
        x += dx;
    }

    public void addY(float dy)
    {
        y += dy;
    }

    public void addZ(float dz)
    {
        z += dz;
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

    public void multiply(float scalar)
    {
        x *= scalar;
        y *= scalar;
        z *= scalar;
    }

    public void normalize()
    {
        x /= length();
        y /= length();
        z /= length();
    }

    public float length()
    {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    /////STATIC OPERATORS (These Don't modify this) /////////////////////////////

    @Contract("_, _ -> new")
    public static @NotNull Vector3f add(@NotNull Vector3f first, @NotNull Vector3f second)
    {
        return new Vector3f(first.x + second.x, first.y + second.y, first.z + second.z);
    }

    @Contract("_, _ -> new")
    public static @NotNull Vector3f subtract(@NotNull Vector3f first, @NotNull Vector3f second)
    {
        return new Vector3f(first.x - second.x, first.y - second.y, first.z - second.z);
    }

    @Contract("_, _ -> new")
    public static @NotNull Vector3f multiply(@NotNull Vector3f first, float scalar)
    {
        return new Vector3f(first.x * scalar, first.y * scalar, first.z * scalar);
    }

    @Contract("_ -> new")
    public static @NotNull Vector3f negate(@NotNull Vector3f vector)
    {
        return new Vector3f(-vector.x, -vector.y, -vector.z);
    }

    @Contract(pure = true)
    public static float slope(@NotNull Vector3f first, @NotNull Vector3f second)
    {
        return (second.y - first.y) / (second.x - first.x);
    }

    @Contract("_ -> new")
    public static @NotNull Vector3f normalize(@NotNull Vector3f vector)
    {
        return new Vector3f(vector.x / vector.length(), vector.y / vector.length(), vector.z / vector.length());
    }

    @Contract(pure = true)
    public static float dot(@NotNull Vector3f first, @NotNull Vector3f second)
    {
        return first.x * second.x + first.y * second.y + first.z * second.z;
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull Vector3f cross(@NotNull Vector3f a, @NotNull Vector3f b)
    {
        return new Vector3f(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
    }

    public void print()
    {
        MAIN_LOGGER.info("X: " + x + " Y: " + y + " Z: " + z);
    }
}
