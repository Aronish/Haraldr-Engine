package main.java;

import main.java.math.Matrix4f;
import main.java.math.Vector3f;

/**
 * A virtual camera for the game. Is essentially a normal object except that the transformation matrix is inverted.
 * That matrix is then applied to all objects in the scene to make it appear as if the camera was moving.
 */
public class Camera{

    public static Matrix4f viewMatrix;
    public static float scale;
    private static Vector3f position;
    private static float rotation;

    private static final float MIN_SCALE = 0.25f;
    private static final double SCALE_SPEED = 1.0d;

    /**
     * Default constructor if no arguments are provided.
     */
    Camera(){
        this(new Vector3f(), 0.0f, 1.0f);
    }

    /**
     * Constructor with parameters for position and rotation.
     * @param pos the position of the camera, an origin vector.
     * @param rot the rotation of the camera around the z-axis, in degrees.
     */
    private Camera(Vector3f pos, float rot, float scal){
        position = pos;
        rotation = rot;
        scale = scal;
        calculateViewMatrix();
    }

    /**
     * Calculates a new view matrix (an inverted transformation matrix) from the current attribute values.
     */
    private static void calculateViewMatrix(){
        viewMatrix = new Matrix4f().transform(position, rotation, scale, true);
    }

    /**
     * Calculates the scale/zoom based on the delta time from Main#update. Also takes scale into account to make the speed constant.
     * Constants are defined in the top of this class.
     * @param shouldIncrease whether the scale should increase or not. If true, it increases, meaning zooming in.
     * @param deltaTime the delta time used to make the scaling uniform.
     */
    static void calculateScale(boolean shouldIncrease, double deltaTime){
        if (shouldIncrease){
            scale += SCALE_SPEED * scale * deltaTime;
        }else {
            if (scale > MIN_SCALE){
                scale -= SCALE_SPEED * scale * deltaTime;
            }else{
                scale = MIN_SCALE;
            }
        }
        calculateViewMatrix();
    }

    /**
     * Sets the position of the camera.
     * @param pos the new position vector.
     */
    public static void setPosition(Vector3f pos){
        position = pos;
        calculateViewMatrix();
    }

    /**
     * Adds the specified vector to the current position.
     * @param pos the position to add.
     */
    public static void addPosition(Vector3f pos){
        position.x += pos.x;
        position.y += pos.y;
        position.z += pos.z;
        calculateViewMatrix();
    }
}
