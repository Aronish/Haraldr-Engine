package main.java;

import main.java.math.Matrix4f;
import main.java.math.Vector3f;

public class Camera{

    private static Vector3f position;
    public static Matrix4f viewMatrix;
    static double velocity;

    Camera(){
        this(new Vector3f(0.0f, 0.0f, 0.0f));
    }

    private Camera(Vector3f pos){
        position = pos;
        velocity = 5.0f;
        calculateViewMatrix();
    }

    private static void calculateViewMatrix(){
        viewMatrix = new Matrix4f().translate(position, true);
    }

    static void calculateXPosition(boolean x, double deltaTime){
        if(x){
            position.x += velocity * deltaTime;
        }else{
            position.x -= velocity * deltaTime;
        }
        calculateViewMatrix();
    }

    static void calculateYPosition(boolean y, double deltaTime){
        if(y){
            position.y += velocity * deltaTime;
        }else{
            position.y -= velocity * deltaTime;
        }
        calculateViewMatrix();
    }

    static void setPos(Vector3f pos){
        position = pos;
        calculateViewMatrix();
    }
}
