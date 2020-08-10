package haraldr.physics;

import haraldr.math.Vector3f;

public class Physics3D
{
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
