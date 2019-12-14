package physics;

import math.Vector3f;

/**
 * Class that represents an axis-aligned bounding box.
 */
public class AABB
{
    private float width = 1.0f, height = 1.0f;
    private Vector3f middle;

    public AABB()
    {
        middle = new Vector3f(0.5f, -0.5f);
    }

    public AABB(float width, float height)
    {
        this.width = width;
        this.height = height;
        calculateMiddle();
    }

    private void calculateMiddle()
    {
        middle = new Vector3f(width / 2.0f, -(height / 2.0f));
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
