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
    public static @NotNull Quaternion fromEulerAngles(@NotNull Vector3f eulerDegrees)
    {
        double cy = Math.cos((float)Math.toRadians(eulerDegrees.getZ()) * 0.5f);
        double sy = Math.sin((float)Math.toRadians(eulerDegrees.getZ()) * 0.5f);
        double cp = Math.cos((float)Math.toRadians(eulerDegrees.getY()) * 0.5f);
        double sp = Math.sin((float)Math.toRadians(eulerDegrees.getY()) * 0.5f);
        double cr = Math.cos((float)Math.toRadians(eulerDegrees.getX()) * 0.5f);
        double sr = Math.sin((float)Math.toRadians(eulerDegrees.getX()) * 0.5f);
        return new Quaternion(
                (float) (cr * cp * cy + sr * sp * sy),
                (float) (sr * cp * cy - cr * sp * sy),
                (float) (cr * sp * cy + sr * cp * sy),
                (float) (cr * cp * sy - sr * sp * cy)
        );
    }

    public static Vector3f toEulerAngles(Quaternion quaternion)
    {
        Vector3f angles = new Vector3f();

        // roll (x-axis rotation)
        double sinr_cosp = 2 * (quaternion.w * quaternion.x + quaternion.y * quaternion.z);
        double cosr_cosp = 1 - 2 * (quaternion.x * quaternion.x + quaternion.y * quaternion.y);
        angles.setX((float)Math.atan2(sinr_cosp, cosr_cosp));

        // pitch (y-axis rotation)
        double sinp = 2 * (quaternion.w * quaternion.y - quaternion.z * quaternion.x);
        if (Math.abs(sinp) >= 1)
        {
            angles.setY((float)Math.copySign(Math.PI / 2f, sinp)); // use 90 degrees if out of range
        } else
        {
            angles.setY((float)Math.asin(sinp));
        }

        // yaw (z-axis rotation)
        double siny_cosp = 2 * (quaternion.w * quaternion.z + quaternion.x * quaternion.y);
        double cosy_cosp = 1 - 2 * (quaternion.y * quaternion.y + quaternion.z * quaternion.z);
        angles.setZ((float)Math.atan2(siny_cosp, cosy_cosp));

        return new Vector3f((float)Math.toDegrees(angles.getX()), (float)Math.toDegrees(angles.getY()), (float)Math.toDegrees(angles.getZ()));
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
