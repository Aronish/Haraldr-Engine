package main.java.math;

import main.java.debug.Logger;

/**
 * A simple class to represent a 3D vector of floats.
 */
public class Vector3f {

    private float x, y, z;

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

    public float getX(){
        return this.x;
    }

    public float getY(){
        return this.y;
    }

    public float getZ(){
        return this.z;
    }

    /**
     * Add the other vector to this vector.
     * @param other the other vector to add.
     * @return the sum Vector.
     */
    public Vector3f add(Vector3f other){
        return new Vector3f(this.x + other.getX(), this.y + other.getY(), this.z + other.getZ());
    }

    public void addThis(Vector3f other){
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
    }

    /**
     * Subtract the other vector from this vector.
     * @param other the other vector to subtract.
     * @return the difference Vector.
     */
    public Vector3f subtract(Vector3f other){
        return new Vector3f(this.x - other.getX(), this.y - other.getY(), this.z - other.getZ());
    }

    public Vector3f subtractY(float dy){
        return new Vector3f(this.x, this.y - dy);
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
     * Prints this vector for debugging purposes.
     */
    public void printVector(){
        Logger.setInfoLevel();
        Logger.log("X: " + this.x + " Y: " + this.y + " Z: " + this.z);
    }
}
