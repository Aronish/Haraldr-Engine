package haraldr.math;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Quaternion
{
    public static final Quaternion IDENTITY = fromAxis(Vector3f.UP, 0f);

    private float w, x, y, z;

    public Quaternion(float w, float x, float y, float z)
    {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Contract("_, _ -> new")
    public static @NotNull Quaternion fromAxis(@NotNull Vector3f axis, float angle)
    {
        axis.normalize();
        float halfAngle = (float) Math.toRadians(angle * 0.5f);
        float sinHalfAngle = (float) Math.sin(halfAngle);
        return new Quaternion((float) Math.cos(halfAngle), axis.getX() * sinHalfAngle, axis.getY() * sinHalfAngle, axis.getZ() * sinHalfAngle);
    }

    @Contract("_ -> new")
    public static @NotNull Quaternion fromEulerAngles(@NotNull Vector3f rotation)
    {
        double cy = Math.cos(rotation.getZ() * 0.5f);
        double sy = Math.sin(rotation.getZ() * 0.5f);
        double cp = Math.cos(rotation.getY() * 0.5f);
        double sp = Math.sin(rotation.getY() * 0.5f);
        double cr = Math.cos(rotation.getX() * 0.5f);
        double sr = Math.sin(rotation.getX() * 0.5f);
        return new Quaternion(
                (float) (cr * cp * cy + sr * sp * sy),
                (float) (sr * cp * cy - cr * sp * sy),
                (float) (cr * sp * cy + sr * cp * sy),
                (float) (cr * cp * sy - sr * sp * cy)
        );
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
