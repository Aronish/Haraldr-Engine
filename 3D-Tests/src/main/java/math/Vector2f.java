package main.java.math;

import main.java.debug.Logger;

public class Vector2f {

    private float x, y;

    public Vector2f(){
        this.x = 0.0f;
        this.y = 0.0f;
    }

    public Vector2f(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void setX(){
        this.x = 0.0f;
    }

    public void setY(){
        this.y = 0.0f;
    }

    public void setX(float x){
        this.x = x;
    }

    public void setY(float y){
        this.y = y;
    }

    public void reset(){
        this.x = 0.0f;
        this.y = 0.0f;
    }

    public float getX(){
        return this.x;
    }

    public float getY(){
        return this.y;
    }

    public void add(Vector2f other){
        this.x += other.getX();
        this.y += other.getY();
    }

    public void addX(float dx){
        this.x += dx;
    }

    public void addY(float dy){
        this.y += dy;
    }

    public void subtractX(float dx){
        this.x -= dx;
    }

    /**
     * Prints this vector for debugging purposes.
     */
    public void printVector(){
        Logger.setInfoLevel();
        Logger.log("X: " + this.x + " Y: " + this.y);
    }
}
