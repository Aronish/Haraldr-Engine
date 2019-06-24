package main.java;

import main.java.graphics.TexturedModel;
import main.java.level.Entity;
import main.java.math.Matrix4f;
import main.java.math.Vector2f;
import main.java.math.Vector3f;

import java.util.HashSet;

/**
 * A virtual camera for the game. Is essentially a normal object except that the transformation matrix is inverted.
 * That matrix is then applied to all objects in the scene to make it appear as if the camera was moving.
 */
public class Camera{

    public static Matrix4f viewMatrix;
    private static Vector3f position;
    private static float rotation;
    public static float scale;

    private static final float MIN_SCALE = 0.30f;
    private static final float MAX_SCALE = 2.0f;
    private static final float SCALE_SPEED = 1.0f;

    /**
     * Default constructor if no arguments are provided.
     */
    Camera(){
        this(new Vector3f(), 0.0f, 1.0f);
    }

    /**
     * Constructor with just the scale.
     * @param scale the scale to scale the world by.
     */
    Camera(float scale){
        this(new Vector3f(), 0.0f, scale);
    }

    /**
     * Constructor with parameters for position and rotation. Can probably be private, since it's always set to the position of the Player.
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
        viewMatrix = new Matrix4f().transform(position, rotation, new Vector2f(scale), true);
    }

    /**
     * Calculates the scale/zoom based on the delta time from Main#update. Also takes scale into account to make the speed constant.
     * Constants are defined in the top of this class.
     * @param shouldIncrease whether the scale should increase or not. If true, it increases, meaning zooming in.
     * @param deltaTime the delta time used to make the scaling uniform.
     */
    static void calculateScale(boolean shouldIncrease, float deltaTime){
        if (shouldIncrease){
            scale += SCALE_SPEED * scale * deltaTime;
            if (scale > MAX_SCALE){
                scale = MAX_SCALE;
            }
        }else {
            scale -= SCALE_SPEED * scale * deltaTime;
            if (scale < MIN_SCALE){
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
        position = pos.multiply(scale);
        calculateViewMatrix();
    }

    /**
     * Adds the specified vector to the current position.
     * @param pos the position to add.
     */
    public static void addPosition(Vector3f pos){
        position.addThis(pos);
        calculateViewMatrix();
    }

    /**
     * Checks if the provided entities are in the view of the Camera. If they are, they are added to the provided list.
     * @param visibleObjects a list, which keeps track of all the visible entities.
     * @param entities the entities to check visibility against.
     */
    public static void isInView(HashSet<Entity> visibleObjects, Entity... entities){
        float scaleAdjustedX = position.getX() / scale;
        float scaleAdjustedY = position.getY() / scale;
        float xBoundary = 16.0f / scale;
        float yBoundary = 9.0f / scale;
        for (Entity entity : entities){
            for (TexturedModel texturedModel : entity.getTexturedModels()){
                boolean collisionX = entity.getPosition().getX() + texturedModel.getRelativePosition().getX() + texturedModel.getAABB().getWidth() > scaleAdjustedX - xBoundary && entity.getPosition().getX() < scaleAdjustedX + xBoundary;
                boolean collisionY = entity.getPosition().getY() + texturedModel.getRelativePosition().getY() - texturedModel.getAABB().getHeight() < scaleAdjustedY + yBoundary && entity.getPosition().getY() > scaleAdjustedY - yBoundary;
                if (collisionX && collisionY){
                    visibleObjects.add(entity);
                    break;
                }
            }
        }
    }
}
