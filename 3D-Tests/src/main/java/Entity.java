package main.java;
//TODO Fix JavaDoc
import main.java.graphics.AABB;
import main.java.graphics.TexturedModel;
import main.java.math.Matrix4f;
import main.java.math.Vector3f;

import java.util.HashMap;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

/**
 * Class that represents a base game object. Contains common properties and a textured model to render.
 */
public class Entity {

    private HashMap<Integer, TexturedModel> texturedModels; // 0 = First
    private Vector3f position;
    private Matrix4f matrix;
    private AABB aabb;
    private int matrixLocation;
    private float rotation;
    private float scale;

    public Entity(Vector3f position, float rotation, float scale, TexturedModel ... texturedModels){
        this.texturedModels = new HashMap<>();
        for (int texMod = 0; texMod < texturedModels.length; texMod++){
            this.texturedModels.put(texMod, texturedModels[texMod]);
        }
        setAABB();
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        updateMatrix();
    }

    /**
     * Sets the position of this object.
     * @param position the position, represented with a vector from the world origin.
     */
    void setPosition(Vector3f position){
        this.position = position;
        updateMatrix();
    }

    /**
     * Adds a vector to the position of this object.
     * @param position the vector to add to the position.
     */
    void addPosition(Vector3f position){
        this.position.x += position.x;
        this.position.y += position.y;
        this.position.z += position.z;
        updateMatrix();
    }

    /**
     * Sets the rotation around the z-axis of this object.
     * @param rotation the rotation, in degrees.
     */
    public void setRotation(float rotation){
        this.rotation = rotation;
        updateMatrix();
    }

    /**
     * Sets the scale of this object.
     * @param scale the scale multiplier.
     */
    public void setScale(float scale){
        this.scale = scale;
        updateMatrix();
    }

    /**
     * Updates the Model-View-Projection matrix with the current attribute values.
     */
    void updateMatrix(){
        this.matrix = new Matrix4f().MVP(this.position, this.rotation, this.scale);
    }

    public void setMatrixLocation(int id){
        this.matrixLocation = glGetUniformLocation(this.texturedModels.get(id).getShader().getShaderProgram(), "matrix");
    }

    /**
     * Sets the uniform variable in the vertex shader to the current Model-View-Projection matrix.
     */
    public void setUniformMatrix(){
        glUniformMatrix4fv(this.matrixLocation, false, this.matrix.matrix);
    }

    protected void setAABB(){
        this.aabb = new AABB(1.0f, 1.0f);
    }

    void setAABB(float width, float height){
        this.aabb = new AABB(width, height);
    }

    /**
     * Gets the position vector of this object.
     * @return the position vector.
     */
    Vector3f getPosition(){
        return new Vector3f(this.position.x, this.position.y, this.position.z);
    }

    public HashMap<Integer, TexturedModel> getTexturedModels(){
        return this.texturedModels;
    }

    AABB getAABB(){
        return this.aabb;
    }
}
