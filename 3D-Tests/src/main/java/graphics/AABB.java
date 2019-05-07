package main.java.graphics;

import main.java.math.Vector3f;

/**
 * Class that represents an axis-aligned bounding box.
 */
public class AABB {

    private float width, height;
    private Vector3f middle;

    AABB(){
        this.width = 1.0f;
        this.height = 1.0f;
        this.middle = new Vector3f(0.5f, -0.5f);
    }

    /**
     * Constructor that sets the width and height of the bounding box.
     * @param width the width.
     * @param height the height.
     */
    AABB(float width, float height){
        this.width = width;
        this.height = height;
        this.middle = new Vector3f(width / 2.0f, -(height / 2.0f) - 1.0f);
    }

    /**
     * Gets the width.
     * @return the width.
     */
    public float getWidth(){
        return this.width;
    }

    /**
     * Gets the height.
     * @return the height.
     */
    public float getHeight(){
        return this.height;
    }

    public Vector3f getMiddle(){
        return this.middle;
    }
}
