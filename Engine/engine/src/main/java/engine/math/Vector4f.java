package engine.math;

import main.JSONArray;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static engine.main.Application.MAIN_LOGGER;

@SuppressWarnings("unused")
public class Vector4f
{
    private float x, y, z, w;

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

    public Vector4f(@NotNull JSONArray jsonArray)
    {
        x = (float) jsonArray.getDouble(0);
        y = (float) jsonArray.getDouble(1);
        z = (float) jsonArray.getDouble(2);
        w = (float) jsonArray.getDouble(3);
    }

    public void set(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void set(double x, double y, double z, double w)
    {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
        this.w = (float) w;
    }

    public void set(@NotNull Vector4f other)
    {
        x = other.x;
        y = other.y;
        z = other.z;
        w = other.w;
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

    public void setW(float w)
    {
        this.w = w;
    }
    
    public void add(float x, float y, float z, float w)
    {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
    }
    
    public void add(double x, double y, double z, double w)
    {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
    }

    public void add(@NotNull Vector4f other)
    {
        x += other.x;
        y += other.y;
        z += other.z;
        w += other.w;
    }
    
    public void addX(float dx)
    {
        x = dx;
    }

    public void addY(float dy)
    {
        y = dy;
    }

    public void addZ(float dz)
    {
        z = dz;
    }

    public void addW(float dw)
    {
        w = dw;
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


    public void multiply(float scalar)
    {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        w *= scalar;
    }

    public void normalize()
    {
        x /= length();
        y /= length();
        z /= length();
        w /= length();
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
        MAIN_LOGGER.info("X: " + x + " Y: " + y + " Z: " + z);
    }
}
