package main.java;

import com.sun.istack.internal.NotNull;
import main.java.math.Matrix4f;
import main.java.math.Vector3f;

/**
 * A virtual camera for the game. Is essentially a normal object except that the transformation matrix is inverted.
 * That matrix is then applied to all objects in the scene to make it appear as if the camera was moving.
 */
public class Camera{

    public static Matrix4f viewMatrix;
    private static Vector3f position;
    private static float rotation;
    private static double scaleSpeed;
    static float scale;
    static double velocity;

    /**
     * Default constructor if no arguments are provided.
     */
    Camera(){
        this(new Vector3f(0.0f, 0.0f, 0.0f), 0.0f, 1.0f);
    }

    /**
     * Constructor with parameters for position and rotation.
     * @param pos the position of the camera, an origin vector.
     * @param rot the rotation of the camera around the z-axis, in degrees.
     */
    Camera(Vector3f pos, float rot, float scal){
        position = pos;
        rotation = rot;
        scale = scal;
        velocity = 5.0f * scale;
        scaleSpeed = 0.5d;
        calculateViewMatrix();
    }

    /**
     * Calculates a new view matrix (an inverted transformation matrix) from the current attribute values.
     */
    private static void calculateViewMatrix(){
        viewMatrix = new Matrix4f().transform(position, rotation, scale, true);
    }

    /**
     * Calculates the x position based on the velocity and delta time.
     * @param x whether the camera should move towards the positive or negative direction. If true, towards positive.
     * @param deltaTime the delta time from the update method in Main.
     */
    static void calculateXPosition(boolean x, double deltaTime){
        if(x){
            position.x += velocity * deltaTime;
        }else{
            position.x -= velocity * deltaTime;
        }
        calculateViewMatrix();
    }

    /**
     * Calculates the y position based on the velocity and delta time.
     * @param y whether the camera should move towards the positive or negative direction. If true, towards positive.
     * @param deltaTime the delta time from the update method in Main.
     */
    static void calculateYPosition(boolean y, double deltaTime){
        if(y){
            position.y += velocity * deltaTime;
        }else{
            position.y -= velocity * deltaTime;
        }
        calculateViewMatrix();
    }

    static void calculateScale(boolean shouldIncrease, double deltaTime){
        if (shouldIncrease){
            scale += scaleSpeed * deltaTime;
        }else{
            scale -= scaleSpeed * deltaTime;
        }
        calculateViewMatrix();
    }

    /**
     * Sets the position of the camera.
     * @param pos the new position vector.
     */
    static void setPosition(Vector3f pos){
        position = pos;
        calculateViewMatrix();
    }

    static void addPosition(Vector3f pos){
        position.x += pos.x;
        position.y += pos.y;
        position.z += pos.z;
        calculateViewMatrix();
    }

    /**
     * Gets the position of the camera.
     * @return the position of the camera.
     */
    static Vector3f getPosition(){
        return position;
    }
}
