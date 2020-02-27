package engine.math;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Quaternion
{
    private float w, x, y, z;

    public Quaternion(float w, float x, float y, float z)
    {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @NotNull
    @Contract("_, _ -> new")
    public static Quaternion fromAxis(@NotNull Vector3f axis, float angle)
    {
        axis.normalize();
        float halfAngle = (float) Math.toRadians(angle * 0.5f);
        float sinHalfAngle = (float) Math.sin(halfAngle);
        return new Quaternion((float) Math.cos(halfAngle), axis.getX() * sinHalfAngle, axis.getY() * sinHalfAngle, axis.getZ() * sinHalfAngle);
    }

    public void normalize()
    {
        w /= getNorm();
        x /= getNorm();
        y /= getNorm();
        z /= getNorm();
    }

    public float getNorm()
    {
        return (float) Math.sqrt(w * w + x * x + y * y + z * z);
    }

    public float getW()
    {
        return w;
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
}
