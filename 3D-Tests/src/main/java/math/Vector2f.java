package main.java.math;

import main.java.debug.Logger;

public class Vector2f {

    private float x, y;

    /**
     * Default constructor if no arguments are provided.
     */
    public Vector2f(){
        this.x = 0.0f;
        this.y = 0.0f;
    }

    public Vector2f(float both){
        this.x = both;
        this.y = both;
    }

    /**
     * Sets the positions to the provided ones.
     * @param x the x component.
     * @param y the y component.
     */
    public Vector2f(float x, float y){
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the x component to 0.
     */
    public void setX(){
        this.x = 0.0f;
    }

    /**
     * Sets the y component to 0.
     */
    public void setY(){
        this.y = 0.0f;
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
        this.x = 0.0f;
        this.y = 0.0f;
    }

    /**
     * Gets the x value.
     * @return the x value.
     */
    public float getX(){
        return this.x;
    }

    /**
     * Gets the y value.
     * @return the y value.
     */
    public float getY(){
        return this.y;
    }

    /**
     * Add another Vector2d to this one.
     * @param other the other Vector2d to add.
     */
    public void add(Vector2f other){
        this.x += other.getX();
        this.y += other.getY();
    }

    /**
     * Adds a value to x.
     * @param dx the value to add.
     */
    public void addX(float dx){
        this.x += dx;
    }

    /**
     * Adds a value to y.
     * @param dy the value to add.
     */
    public void addY(float dy){
        this.y += dy;
    }

    /**
     * Subtracts a value from x.
     * @param dx the value to subtract.
     */
    public void subtractX(float dx){
        this.x -= dx;
    }

    public void subtractY(float dy){
        this.y -= dy;
    }

    /**
     * Prints this vector for debugging purposes.
     */
    public void printVector(){
        Logger.setInfoLevel();
        Logger.log("X: " + this.x + " Y: " + this.y);
    }
}
