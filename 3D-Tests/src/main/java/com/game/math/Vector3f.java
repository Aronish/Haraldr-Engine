package com.game.math;

import com.game.debug.Logger;

import static com.game.Application.MAIN_LOGGER;

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

    /**
     * @return the x component.
     */
    public float getX(){
        return x;
    }

    /**
     * @return the y component.
     */
    public float getY(){
        return y;
    }

    /**
     * @return the z component.
     */
    float getZ(){
        return z;
    }

    /**
     * Add the other vector to this vector.
     * @param other the other vector to add.
     * @return the sum Vector.
     */
    public Vector3f add(Vector3f other){
        return new Vector3f(x + other.getX(), y + other.getY(), z + other.getZ());
    }

    /**
     * Adds another vector to this vector.
     * @param other the other vector to add.
     */
    public void addThis(Vector3f other){
        x += other.x;
        y += other.y;
        z += other.z;
    }

    /**
     * Subtract the other vector from this vector.
     * @param other the other vector to subtract.
     * @return the difference Vector.
     */
    public Vector3f subtract(Vector3f other){
        return new Vector3f(x - other.getX(), y - other.getY(), z - other.getZ());
    }

    /**
     * Subtracts a value from the y component.
     * @param dy the value to subtract.
     * @return the resulting vector.
     */
    public Vector3f subtractY(float dy){
        return new Vector3f(x, y - dy);
    }

    /**
     * Multiply this Vector with a scalar.
     * @param scalar the scalar to multiply with.
     * @return the product Vector.
     */
    public Vector3f multiply(float scalar){
        return new Vector3f(x * scalar, y * scalar, z * scalar);
    }

    /**
     * Prints this vector for debugging purposes.
     */
    public void printVector(){
        MAIN_LOGGER.info("X: " + x + " Y: " + y + " Z: " + z);
    }
}
