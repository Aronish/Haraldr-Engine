package main.java.math;

/**
 * A simple class to represent a 3D vector of floats.
 */
public class Vector3f {

    public float x, y, z;

    /**
     * Default constructor if no arguments are provided.
     */
    public Vector3f(){
        this(0.0f, 0.0f, 0.0f);
    }

    /**
     * Sets the positions to the provided ones.
     * @param x the x position.
     * @param y the y position.
     * @param z the z position.
     */
    public Vector3f(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
