package main.java.physics;

import main.java.math.Vector2f;
import main.java.math.Vector3f;

/**
 * Class that represents an axis-aligned bounding box.
 */
public class AABB {

    private float width, height;
    private Vector3f middle;

    /**
     * Default constructor without parameters. Sets a 1x1 bounding box.
     */
    public AABB(){
        this.width = 1.0f;
        this.height = 1.0f;
        this.middle = new Vector3f(0.5f, -0.5f);
    }

    /**
     * Constructor that sets the width and height of the bounding box. Calculates the middle of the bounding box.
     * @param width the width.
     * @param height the height.
     */
    public AABB(float width, float height){
        this.width = width;
        this.height = height;
        calculateMiddle();
    }

    /**
     * Accounts for the scale of an Entity.
     * @param scale the scale to multiply by.
     */
    public void setScale(Vector2f scale){
        this.width *= scale.getX();
        this.height *= scale.getY();
        calculateMiddle();
    }

    /**
     * Calculates the middle of this bounding box.
     */
    private void calculateMiddle(){
        this.middle = new Vector3f(this.width / 2.0f, -(this.height / 2.0f));
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

    /**
     * Gets the middle of this bounding box.
     * @return the middle of this bounding box.
     */
    public Vector3f getMiddle(){
        return this.middle;
    }
}
