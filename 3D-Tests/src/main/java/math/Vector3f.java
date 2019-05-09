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
     * Add the other vector to this vector.
     * @param other the other vector to add.
     * @return the sum Vector.
     */
    public Vector3f add(Vector3f other){
        return new Vector3f(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    /**
     * Subtract the other vector from this vector.
     * @param other the other vector to subtract.
     * @return the difference Vector.
     */
    public Vector3f subtract(Vector3f other){
        return new Vector3f(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    /**
     * Multiply this Vector with a scalar.
     * @param scalar the scalar to multiply with.
     * @return the product Vector.
     */
    public Vector3f multiply(float scalar){
        return new Vector3f(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    /**
     * Normalizes the vector. ! NOT SURE THIS IS GOOD !
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
     * Calculates the distance between two (origin) Vector's.
     * @param other the other Vector.
     * @return the distance.
     */
    public double getDistance(Vector3f other){
        Vector3f between = other.subtract(this);
        return Math.sqrt(Math.pow(between.x, 2) + Math.pow(between.y, 2));
    }

    /**
     * Prints this vector for debugging purposes.
     */
    public void printVector(){
        System.out.println("X: " + this.x + " Y: " + this.y + " Z: " + this.z);
    }
}
