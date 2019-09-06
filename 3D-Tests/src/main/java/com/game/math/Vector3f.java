package com.game.math;

import static com.game.Application.MAIN_LOGGER;

/**
 * A simple class to represent a 3D vector of floats.
 */
public class Vector3f {

    private float x, y, z;

    public Vector3f(){}

    public Vector3f(float x, float y){
        this(x, y, 0.0f);
    }

    public Vector3f(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(double x, double y, double z){
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }

    public void set(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void set(Vector3f other)
    {
        x = other.x;
        y = other.y;
    }

    public void setX(float x)
    {
        this.x = x;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getZ(){
        return z;
    }

    /**
     * Adds the other vector to this vector. Does not change this vector.
     * @param other the other vector to add.
     * @return the sum Vector.
     */
    public Vector3f addReturn(Vector3f other){
        return new Vector3f(x + other.getX(), y + other.getY(), z + other.getZ());
    }

    /**
     * Adds another vector to this vector.
     * @param other the other vector to add.
     */
    public void add(Vector3f other){
        x += other.x;
        y += other.y;
        z += other.z;
    }

    /**
     * Subtracts a value from the y component.
     * @param dy the value to subtract.
     * @return the resulting vector.
     */
    public Vector3f subtractY(float dy){
        return new Vector3f(x, y - dy);
    }

    public Vector3f multiply(float scalar){
        return new Vector3f(x * scalar, y * scalar, z * scalar);
    }

    public void printVector(){
        MAIN_LOGGER.info("X: " + x + " Y: " + y + " Z: " + z);
    }
}
