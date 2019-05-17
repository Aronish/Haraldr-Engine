package main.java.math;

public class Vector2d {

    private double x, y;

    public Vector2d(){
        this.x = 0.0d;
        this.y = 0.0d;
    }

    public Vector2d(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void setX(){
        this.x = 0.0d;
    }

    public void setY(){
        this.y = 0.0d;
    }

    public void setX(double x){
        this.x = x;
    }

    public void setY(double y){
        this.y = y;
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public void addX(double dx){
        this.x += dx;
    }

    public void subtractX(double dx){
        this.x -= dx;
    }
}
