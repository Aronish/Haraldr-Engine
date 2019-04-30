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
     * Constructor for just the x and y components.
     * @param x the x component.
     * @param y the y component.
     */
    public Vector3f(float x, float y){
        this(x, y, 0.0f);
    }

    /**
     * Sets the positions to the provided ones.
     * @param x the x component.
     * @param y the y component.
     * @param z the z component.
     */
    public Vector3f(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Constructor with components as doubles. Used in the normalize function.
     * @param x the x component.
     * @param y the y component.
     * @param z the z component.
     */
    public Vector3f(double x, double y, double z){
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }

    /**
     * Normalizes this vector.
     * @return the normalized vector.
     */
    public Vector3f normalize(){
        double sqX = Math.pow((double) this.x, 2);
        double sqY = Math.pow((double) this.y, 2);
        double sqZ = Math.pow((double) this.z, 2);
        double magnitude = Math.sqrt(sqX + sqY + sqZ);
        double norm = 1.0f / magnitude;
        return new Vector3f(this.x *= norm, this.y *= norm, this.z *= norm);
    }

    /**
     * Prints this vector for debugging purposes.
     */
    public void printVector(){
        System.out.println("X: " + this.x + " Y: " + this.y + " Z: " + this.z);
    }
}
