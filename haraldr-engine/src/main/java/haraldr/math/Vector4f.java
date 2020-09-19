package haraldr.math;

import haraldr.debug.Logger;
import jsonparser.JSONArray;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Vector4f
{
    private float x, y, z, w;

    @Contract(pure = true)
    public Vector4f() {}

    public Vector4f(float all)
    {
        this.x = all;
        this.y = all;
        this.z = all;
        this.w = all;
    }

    public Vector4f(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f(Vector3f vector)
    {
        x = vector.getX();
        y = vector.getY();
        z = vector.getZ();
        w = 0f;
    }

    public Vector4f(@NotNull JSONArray jsonArray)
    {
        x = (float) jsonArray.getDouble(0);
        y = (float) jsonArray.getDouble(1);
        z = (float) jsonArray.getDouble(2);
        w = (float) jsonArray.getDouble(3);
    }

    public Vector4f set(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public Vector4f set(double x, double y, double z, double w)
    {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
        this.w = (float) w;
        return this;
    }

    public Vector4f set(@NotNull Vector4f other)
    {
        x = other.x;
        y = other.y;
        z = other.z;
        w = other.w;
        return this;
    }

    public Vector4f setX(float x)
    {
        this.x = x;
        return this;
    }

    public Vector4f setY(float y)
    {
        this.y = y;
        return this;
    }

    public Vector4f setZ(float z)
    {
        this.z = z;
        return this;
    }

    public Vector4f setW(float w)
    {
        this.w = w;
        return this;
    }

    public Vector4f add(float x, float y, float z, float w)
    {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        return this;
    }

    public Vector4f add(double x, double y, double z, double w)
    {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        return this;
    }

    public Vector4f add(@NotNull Vector4f other)
    {
        x += other.x;
        y += other.y;
        z += other.z;
        w += other.w;
        return this;
    }

    public Vector4f addX(float dx)
    {
        x += dx;
        return this;
    }

    public Vector4f addY(float dy)
    {
        y += dy;
        return this;
    }

    public Vector4f addZ(float dz)
    {
        z += dz;
        return this;
    }

    public Vector4f addW(float dw)
    {
        w += dw;
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

    public float getW()
    {
        return w;
    }

    public Vector4f multiply(float scalar)
    {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        w *= scalar;
        return this;
    }

    public Vector4f divide(float scalar)
    {
        x /= scalar;
        y /= scalar;
        z /= scalar;
        w /= scalar;
        return this;
    }

    public Vector4f normalize()
    {
        double length = length();
        x /= length;
        y /= length;
        z /= length;
        w /= length;
        return this;
    }

    public float length()
    {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    /////STATIC OPERATORS (These Don't modify this) /////////////////////////////

    @Contract("_, _ -> new")
    public static @NotNull Vector4f add(@NotNull Vector4f first, @NotNull Vector4f second)
    {
        return new Vector4f(first.x + second.x, first.y + second.y, first.z + second.z, first.w + second.w);
    }

    @Contract("_, _ -> new")
    public static @NotNull Vector4f subtract(@NotNull Vector4f first, float scalar)
    {
        return new Vector4f(first.x - scalar, first.y - scalar, first.z - scalar, first.w - scalar);
    }

    @Contract("_, _ -> new")
    public static @NotNull Vector4f subtract(@NotNull Vector4f first, @NotNull Vector4f second)
    {
        return new Vector4f(first.x - second.x, first.y - second.y, first.z - second.z, first.w - second.w);
    }

    @Contract("_, _ -> new")
    public static @NotNull Vector4f multiply(@NotNull Vector4f first, float scalar)
    {
        return new Vector4f(first.x * scalar, first.y * scalar, first.z * scalar, first.w * scalar);
    }

    @Contract("_ -> new")
    public static @NotNull Vector4f negate(@NotNull Vector4f vector)
    {
        return new Vector4f(-vector.x, -vector.y, -vector.z, -vector.w);
    }

    @Contract("_ -> new")
    public static @NotNull Vector4f normalize(@NotNull Vector4f vector)
    {
        return new Vector4f(vector.x / vector.length(), vector.y / vector.length(), vector.z / vector.length(), vector.w / vector.length());
    }

    @Contract(pure = true)
    public static float dot(@NotNull Vector4f first, @NotNull Vector4f second)
    {
        return first.x * second.x + first.y * second.y + first.z * second.z;
    }

    public void print()
    {
        Logger.info("X: " + x + " Y: " + y + " Z: " + z + " W: " + w);
    }

    @Override
    public String toString()
    {
        return "X: " + x + " Y: " + y + " Z: " + z + " W: " + w;
    }
}
