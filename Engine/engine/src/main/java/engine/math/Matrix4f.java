package engine.math;

import engine.event.WindowResizedEvent;
import org.jetbrains.annotations.NotNull;

import static engine.main.Application.MAIN_LOGGER;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Matrix4f
{
    public static final float FIXED_ORTHOGRAPHIC_AXIS = 9f;
    private static final float NEAR_FAR = 5f;

    public static float dynamicOrthographicAxis;
    public static float scale = 1f;
    private static final boolean fixedWidth = true; //Fixed width is better

    private static final float DEFAULT_FOV = 60f;
    private static float fov = DEFAULT_FOV;
    private static float aspectRatio;

    public static Matrix4f orthographic;
    public static Matrix4f pixelOrthographic;
    public static Matrix4f perspective;

    public float[] matrix = new float[16]; //Stored in column major both in memory and value-wise.

    public static void onResize(@NotNull WindowResizedEvent event)
    {
        aspectRatio = (float) event.width / event.height;
        recalculateOrthographic(aspectRatio);
        recalculatePixelOrthographic(event.width, event.height);
        recalculatePerspective(aspectRatio);
    }

    public static void init(int width, int height)
    {
        aspectRatio = (float) width / height;
        recalculateOrthographic(aspectRatio);
        recalculatePixelOrthographic(width, height);
        recalculatePerspective(aspectRatio);
    }

    @NotNull
    public static Matrix4f identity()
    {
        Matrix4f identity = new Matrix4f();
        identity.matrix[0] = 1.0f;
        identity.matrix[5] = 1.0f;
        identity.matrix[10] = 1.0f;
        identity.matrix[15] = 1.0f;
        return identity;
    }

    @NotNull
    public Matrix4f multiply(Matrix4f multiplicand) // Does not affect current matrix
    {
        Matrix4f result = identity();
        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 4; x++)
            {
                float sum = 0.0f;
                for (int e = 0; e < 4; e++)
                {
                    sum += matrix[x + e * 4] * multiplicand.matrix[e + y * 4];
                }
                result.matrix[x + y * 4] = sum;
            }
        }
        return result;
    }

    @NotNull
    public Vector3f multiply(@NotNull Vector3f multiplicand)
    {
        float[] start = { multiplicand.getX(), multiplicand.getY(), multiplicand.getZ(), 1.0f };
        float[] result = new float[4];
        for (int x = 0; x < 4; x++)
        {
            float sum = 0.0f;
            for (int e = 0; e < 4; e++)
            {
                sum += matrix[x + e * 4] * start[e];
            }
            result[x] = sum;
        }
        return new Vector3f(result[0], result[1], result[2]);
    }

    @NotNull
    public Vector4f multiply(@NotNull Vector4f multiplicand)
    {
        float[] start = { multiplicand.getX(), multiplicand.getY(), multiplicand.getZ(), multiplicand.getW() };
        float[] result = new float[4];
        for (int x = 0; x < 4; x++)
        {
            float sum = 0.0f;
            for (int e = 0; e < 4; e++)
            {
                sum += matrix[x + e * 4] * start[e];
            }
            result[x] = sum;
        }
        return new Vector4f(result[0], result[1], result[2], result[3]);
    }

    public static void setZoom(float zoom)
    {
        scale = zoom;
        fov = zoom;
        recalculateOrthographic(FIXED_ORTHOGRAPHIC_AXIS / dynamicOrthographicAxis);
        recalculatePerspective(aspectRatio);
    }

    public static void addZoom(float zoom)
    {
        scale += zoom;
        fov += zoom;
        if (scale < 0f) scale = 0f; // Avoids flipping everything
        if (fov < 10f) fov = 10f;
        recalculateOrthographic(FIXED_ORTHOGRAPHIC_AXIS / dynamicOrthographicAxis);
        recalculatePerspective(aspectRatio);
    }

    public static void resetZoom()
    {
        scale = 1f;
        fov = DEFAULT_FOV;
        recalculateOrthographic(FIXED_ORTHOGRAPHIC_AXIS / dynamicOrthographicAxis);
        recalculatePerspective(aspectRatio);
    }

    @NotNull
    public static Matrix4f transform(Vector3f position, float angle, @NotNull Vector2f scale, boolean isCamera)
    {
        return translate(scale.getX() == -1 ? Vector3f.add(position, new Vector3f(1.0f, 0.0f)) : position, isCamera).multiply(scale(scale));
    }

    /////UNUSED WITH PIXELORTHOGRAPHIC////////////////////////////////////////////////////////////////////////
        @NotNull
        private static Matrix4f transformPixelSpace(Vector3f pixelPosition, Vector2f scale, int width, int height)
        {
            return translate(clampToUnitSpace(pixelPosition, width, height), false).multiply(scale(scale));
        }

        @NotNull
        private static Vector3f clampToUnitSpace(@NotNull Vector3f pixelPosition, int width, int height)
        {
            return new Vector3f(
                    (pixelPosition.getX() / width) * (2 * (fixedWidth ? FIXED_ORTHOGRAPHIC_AXIS : dynamicOrthographicAxis)) - (fixedWidth ? FIXED_ORTHOGRAPHIC_AXIS : dynamicOrthographicAxis),
                    (-pixelPosition.getY() / height) * (2 * (fixedWidth ? dynamicOrthographicAxis : FIXED_ORTHOGRAPHIC_AXIS)) + (fixedWidth ? dynamicOrthographicAxis : FIXED_ORTHOGRAPHIC_AXIS)
            );
        }
    /////UNUSED WITH PIXELORTHOGRAPHIC/////////////////////////////////////////////////////////////////////////

    @NotNull
    public static Matrix4f scale(@NotNull Vector2f scale)
    {
        Matrix4f result = new Matrix4f();
        result.matrix[0] = scale.getX();
        result.matrix[5] = scale.getY();
        result.matrix[10] = 1.0f;
        result.matrix[15] = 1.0f;
        return result;
    }

    @NotNull
    public static Matrix4f scale(@NotNull Vector3f scale)
    {
        Matrix4f result = new Matrix4f();
        result.matrix[0] = scale.getX();
        result.matrix[5] = scale.getY();
        result.matrix[10] = scale.getZ();
        result.matrix[15] = 1.0f;
        return result;
    }

    @NotNull
    public static Matrix4f translate(Vector3f vector, boolean isCamera)
    {
        Matrix4f result = identity();
        if (isCamera)
        {
            result.matrix[12] = -vector.getX();
            result.matrix[13] = -vector.getY();
            result.matrix[14] = -vector.getZ();
        }else{
            result.matrix[12] = vector.getX();
            result.matrix[13] = vector.getY();
            result.matrix[14] = vector.getZ();
        }
        return result;
    }

    @NotNull
    public static Matrix4f rotate(@NotNull Vector3f rotation)
    {
        return rotateX(rotation.getX()).multiply(rotateY(rotation.getY()).multiply(rotateZ(rotation.getZ())));
    }

    @NotNull
    public static Matrix4f rotateX(float angle)
    {
        Matrix4f result = identity();
        float radians = (float) Math.toRadians(angle);
        float cosAngle = (float) Math.cos(radians);
        float sinAngle = (float) Math.sin(radians);
        result.matrix[5] = cosAngle;
        result.matrix[6] = sinAngle;
        result.matrix[9] = -sinAngle;
        result.matrix[10] = cosAngle;
        return result;
    }

    @NotNull
    public static Matrix4f rotateY(float angle)
    {
        Matrix4f result = identity();
        float radians = (float) Math.toRadians(angle);
        float cosAngle = (float) Math.cos(radians);
        float sinAngle = (float) Math.sin(radians);
        result.matrix[0] = cosAngle;
        result.matrix[2] = -sinAngle;
        result.matrix[8] = sinAngle;
        result.matrix[10] = cosAngle;
        return result;
    }

    @NotNull
    public static Matrix4f rotateZ(float angle)
    {
        Matrix4f result = identity();
        float radians = (float) Math.toRadians(angle);
        float cosAngle = (float) Math.cos(radians);
        float sinAngle = (float) Math.sin(radians);
        result.matrix[0] = cosAngle;
        result.matrix[1] = sinAngle;
        result.matrix[4] = -sinAngle;
        result.matrix[5] = cosAngle;
        return result;
    }

    @NotNull
    public static Matrix4f rotate(Vector3f axis, float angle)
    {
        Matrix4f result = new Matrix4f();
        Quaternion quaternion = Quaternion.fromAxis(axis, angle);
        quaternion.normalize();
        result.matrix[0] = 1 - 2 * (quaternion.getY() * quaternion.getY() + quaternion.getZ() * quaternion.getZ());
        result.matrix[1] = 2 * (quaternion.getX() * quaternion.getY() + quaternion.getZ() * quaternion.getW());
        result.matrix[2] = 2 * (quaternion.getX() * quaternion.getZ() - quaternion.getY() * quaternion.getW());
        result.matrix[4] = 2 * (quaternion.getX() * quaternion.getY() - quaternion.getZ() * quaternion.getW());
        result.matrix[5] = 1 - 2 * (quaternion.getX() * quaternion.getX() + quaternion.getZ() * quaternion.getZ());
        result.matrix[6] = 2 * (quaternion.getY() * quaternion.getZ() + quaternion.getX() * quaternion.getW());
        result.matrix[8] = 2 * (quaternion.getX() * quaternion.getZ() + quaternion.getY() * quaternion.getW());
        result.matrix[9] = 2 * (quaternion.getY() * quaternion.getZ() - quaternion.getX() * quaternion.getW());
        result.matrix[10] = 1 - 2 * (quaternion.getX() * quaternion.getX() + quaternion.getY() * quaternion.getY());
        result.matrix[15] = 1;
        return result;
    }

    @NotNull
    private static Matrix4f orthographic(float right, float left, float top, float bottom, float far, float near)
    {
        Matrix4f result = identity();
        result.matrix[0] = 2.0f / (right - left);
        result.matrix[5] = 2.0f / (top - bottom);
        result.matrix[10] = -2.0f / (far - near);
        result.matrix[12] = -((right + left) / (right - left));
        result.matrix[13] = -((top + bottom) / (top - bottom));
        result.matrix[14] = -((far + near) / (far - near));
        return result;
    }

    private static void recalculateOrthographic(float aspectRatio)
    {
        if (fixedWidth)
        {
            recalcOrthoFixedWidth(aspectRatio);
        }else{
            recalcOrthoFixedHeight(aspectRatio);
        }
    }

    private static void recalcOrthoFixedWidth(float aspectRatio)
    {
        dynamicOrthographicAxis = FIXED_ORTHOGRAPHIC_AXIS / aspectRatio;
        orthographic = orthographic(FIXED_ORTHOGRAPHIC_AXIS * scale, -FIXED_ORTHOGRAPHIC_AXIS * scale, dynamicOrthographicAxis * scale, -dynamicOrthographicAxis * scale, -NEAR_FAR, NEAR_FAR);
    }

    private static void recalcOrthoFixedHeight(float aspectRatio)
    {
        dynamicOrthographicAxis = FIXED_ORTHOGRAPHIC_AXIS * aspectRatio;
        orthographic = orthographic(dynamicOrthographicAxis * scale, -dynamicOrthographicAxis * scale, FIXED_ORTHOGRAPHIC_AXIS * scale, -FIXED_ORTHOGRAPHIC_AXIS * scale, -NEAR_FAR, NEAR_FAR);
    }

    private static void recalculatePixelOrthographic(int width, int height)
    {
        pixelOrthographic = orthographic(width, 0, 0, height, -NEAR_FAR, NEAR_FAR);
    }

    public static void recalculatePerspective(float aspectRatio)
    {
        perspective = perspective(fov, aspectRatio);
    }

    @NotNull
    public static Matrix4f perspective(float fov, float aspectRatio)
    {
        //Camera looks towards positive z to begin with.
        Matrix4f result = new Matrix4f();
        float near = 0.5f;
        float far = 20f;
        float range = far - near;
        float tanHalfFov = (float) Math.tan(Math.toRadians(fov / 2));

        result.matrix[0] = 1.0f / (tanHalfFov * aspectRatio);
        result.matrix[5] = 1.0f / tanHalfFov;
        result.matrix[10] = -(far + near) / range;
        result.matrix[11] = -2f * far * near / range;
        result.matrix[14] = -1; // It is apparently this way around.
        return result;
    }

    @NotNull
    public static Matrix4f lookAt(@NotNull Vector3f position, @NotNull Vector3f target, @NotNull Vector3f up)
    {
        Matrix4f result = identity();
        Vector3f direction = Vector3f.normalize(Vector3f.subtract(position, target));
        Vector3f right = Vector3f.normalize(Vector3f.cross(up, direction));
        Vector3f realUp = Vector3f.cross(direction, right);
        result.matrix[0] = right.getX();
        result.matrix[4] = right.getY();
        result.matrix[8] = right.getZ();
        result.matrix[1] = realUp.getX();
        result.matrix[5] = realUp.getY();
        result.matrix[9] = realUp.getZ();
        result.matrix[2] = direction.getX();
        result.matrix[6] = direction.getY();
        result.matrix[10] = direction.getZ();
        return result.multiply(translate(position, true));
    }

    public void print()
    {
        System.out.println("-------------------------------------");
        for (int x = 0; x < 4; ++x)
        {
            for (int e = 0; e < 4; ++e)
            {
                System.out.printf("%-10f", matrix[e * 4 + x]);
            }
            System.out.println();
        }
    }
}