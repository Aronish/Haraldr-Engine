package haraldr.physics;

import haraldr.math.Matrix4f;
import haraldr.math.Vector2f;
import haraldr.math.Vector3f;
import haraldr.math.Vector4f;

public class Physics3D
{
    public static Vector3f castRayFromMouse(Vector2f mousePosition, Vector2f windowSize, Matrix4f viewMatrix, Matrix4f projectionMatrix)
    {
        Vector4f rayClipSpace = new Vector4f(
                (2f * mousePosition.getX()) / windowSize.getX() - 1f,
                1f - (2f * mousePosition.getY()) / windowSize.getY(),
                -1f,
                1f
        );
        Vector4f rayEyeSpace = Matrix4f.multiply(Matrix4f.invert(projectionMatrix), rayClipSpace);
        rayEyeSpace.setZ(-1f);
        rayEyeSpace.setW(0f);

        return new Vector3f(Matrix4f.multiply(Matrix4f.invert(viewMatrix), rayEyeSpace)).normalize();
    }

    public static boolean rayIntersectsSphere(Vector3f rayOrigin, Vector3f rayDirection, Vector3f sphereCenter, float radius)
    {
        Vector3f oMinusC = Vector3f.subtract(rayOrigin, sphereCenter);
        float b = Vector3f.dot(rayDirection, oMinusC);
        float c = Vector3f.dot(oMinusC, oMinusC) - radius * radius;
        float determinant = b * b - c;
        if (determinant >= 0)
        {
            float t1 = (float) (-b + Math.sqrt(determinant));
            float t2 = (float) (-b - Math.sqrt(determinant));
            return !(t1 < 0) && !(t2 < 0);
        }
        return false;
    }
}
