package haraldr.physics;

import haraldr.math.Vector3f;

@Deprecated
public class AABB
{
    private float width = 1f, height = 1f;
    private Vector3f middle;

    public AABB()
    {
        middle = new Vector3f(0.5f, -0.5f, 0f);
    }

    public AABB(float width, float height)
    {
        this.width = width;
        this.height = height;
        calculateMiddle();
    }

    private void calculateMiddle()
    {
        middle = new Vector3f(width / 2f, -(height / 2f), 0f);
    }

    public float getWidth()
    {
        return width;
    }

    public float getHeight()
    {
        return height;
    }

    public Vector3f getMiddle()
    {
        return middle;
    }
}
