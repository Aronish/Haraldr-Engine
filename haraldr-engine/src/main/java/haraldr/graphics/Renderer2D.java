package haraldr.graphics;

import haraldr.math.Matrix4f;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class Renderer2D
{
    public static Matrix4f pixelOrthographic;

    public static void init(int windowWidth, int windowHeight)
    {
        pixelOrthographic = Matrix4f.orthographic(0, windowWidth, windowHeight, 0, -1f, 1f);
    }

    public static void onWindowResized(int width, int height)
    {
        pixelOrthographic = Matrix4f.orthographic(0, width, height, 0, -1, 1f);
    }
}