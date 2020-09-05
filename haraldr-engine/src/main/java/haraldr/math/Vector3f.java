package haraldr.math;

import haraldr.debug.Logger;
import jsonparser.JSONArray;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Vector3f
{
    public static final Vector3f IDENTITY   = new Vector3f();
    public static final Vector3f UP         = new Vector3f(0f, 1f, 0f);

    private float x, y, z;

    @Contract(pure = true)
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

    public Vector3f(Vector4f vector)
    {
        x = vector.getX();
        y = vector.getY();
        z = vector.getZ();
    }

    public Vector3f(@NotNull JSONArray jsonArray)
    {
        x = (float) jsonArray.getDouble(0);
        y = (float) jsonArray.getDouble(1);
        z = (float) jsonArray.getDouble(2);
    }

    public Vector3f set(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3f set(double x, double y, double z)
    {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
        return this;
    }

    public Vector3f set(float all)
    {
        x = all;
        y = all;
        z = all;
        return this;
    }

    public Vector3f set(double all)
    {
        x = (float) all;
        y = (float) all;
        z = (float) all;
        return this;
    }

    public Vector3f set(@NotNull Vector3f other)
    {
        x = other.x;
        y = other.y;
        z = other.z;
        return this;
    }

    public Vector3f setX(float x)
    {
        this.x = x;
        return this;
    }

    public Vector3f setY(float y)
    {
        this.y = y;
        return this;
    }

    public Vector3f setZ(float z)
    {
        this.z = z;
        return this;
    }

    public Vector3f add(float all)
    {
        x += all;
        y += all;
        z += all;
        return this;
    }

    public Vector3f add(double all)
    {
        x += all;
        y += all;
        z += all;
        return this;
    }

    public Vector3f add(@NotNull Vector3f other)
    {
        x += other.x;
        y += other.y;
        z += other.z;
        return this;
    }

    public Vector3f addX(float dx)
    {
        x += dx;
        return this;
    }

    public Vector3f addY(float dy)
    {
        y += dy;
        return this;
    }

    public Vector3f addZ(float dz)
    {
        z += dz;
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

    public float getZ()
    {
        return z;
    }

    public Vector3f multiply(float scalar)
    {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }

    public Vector3f multiply(Vector3f other)
    {
        x *= other.x;
        y *= other.y;
        z *= other.z;
        return this;
    }

    public Vector3f normalize()
    {
        double length = length();
        x /= length;
        y /= length;
        z /= length;
        return this;
    }

    public double length()
    {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /////STATIC OPERATORS (These don't modify this) //////////////////////////////////////

    @Contract("_, _ -> new")
    public static @NotNull Vector3f add(@NotNull Vector3f first, float scalar)
    {
        return new Vector3f(first.x + scalar, first.y + scalar, first.z + scalar);
    }

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

    @Contract("_, _ -> new")
    public static @NotNull Vector3f multiply(@NotNull Vector3f first, @NotNull Vector3f second)
    {
        return new Vector3f(first.x * second.x, first.y * second.y, first.z * second.z);
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
        Logger.info("X: " + x + " Y: " + y + " Z: " + z);
    }
}
