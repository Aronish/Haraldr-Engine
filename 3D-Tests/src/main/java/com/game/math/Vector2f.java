package com.game.math;

import com.game.debug.Logger;

/**
 * A simple class to represent a 2D vector of floats.
 */
public class Vector2f {

    private float x, y;

    /**
     * Default constructor if no arguments are provided.
     */
    public Vector2f(){
        x = 0.0f;
        y = 0.0f;
    }

    /**
     * Constructor that sets both x and y to the same value.
     * @param both the value to apply to both x and y.
     */
    public Vector2f(float both){
        x = both;
        y = both;
    }

    /**
     * Sets the components to the provided ones.
     * @param x the x component.
     * @param y the y component.
     */
    public Vector2f(float x, float y){
        this.x = x;
        this.y = y;
    }

    /**
     * Sets both components.
     * @param x the x component.
     * @param y the y component.
     */
    public void set(float x, float y){
        this.x = x;
        this.y = y;
    }

    /**
     * Sets both components to the same value.
     * @param both the value of both components.
     */
    public void setBoth(float both){
        x = both;
        y = both;
    }

    /**
     * Sets the x component to 0.
     */
    public void setX(){
        x = 0.0f;
    }

    /**
     * Sets the y component to 0.
     */
    public void setY(){
        y = 0.0f;
    }

    /**
     * Sets the x component to the provided value.
     * @param x the value of x.
     */
    public void setX(float x){
        this.x = x;
    }

    /**
     * Sets the y component to the provided value.
     * @param y the value of y.
     */
    public void setY(float y){
        this.y = y;
    }

    /**
     * Resets the x and y values to 0.
     */
    public void reset(){
        x = 0.0f;
        y = 0.0f;
    }

    /**
     * @return the x value.
     */
    public float getX(){
        return x;
    }

    /**
     * @return the y value.
     */
    public float getY(){
        return y;
    }

    /**
     * Add another Vector2d to this one.
     * @param other the other Vector2d to add.
     */
    public void add(Vector2f other){
        x += other.getX();
        y += other.getY();
    }

    /**
     * Adds a value to x.
     * @param dx the value to add.
     */
    public void addX(float dx){
        x += dx;
    }

    /**
     * Adds a value to y.
     * @param dy the value to add.
     */
    public void addY(float dy){
        y += dy;
    }

    /**
     * Subtracts a value from x.
     * @param dx the value to subtract.
     */
    public void subtractX(float dx){
        x -= dx;
    }

    /**
     * Subtracts a value from y.
     * @param dy the value to subtract.
     */
    public void subtractY(float dy){
        y -= dy;
    }

    /**
     * Prints this vector for debugging purposes.
     */
    public void printVector(){
        Logger.info("X: " + x + " Y: " + y);
    }
}
