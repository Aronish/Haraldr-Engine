package main.java.graphics;

/**
 * Class that represents an axis-aligned bounding box.
 */
public class AABB {

    private float width, height;

    /**
     * Constructor that sets the width and height of the bounding box.
     * @param width the width.
     * @param height the height.
     */
    public AABB(float width, float height){
        this.width = width;
        this.height = height;
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
}
