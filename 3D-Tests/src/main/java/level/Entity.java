package main.java.level;

import main.java.graphics.TexturedModel;
import main.java.math.Matrix4f;
import main.java.math.Vector2f;
import main.java.math.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class that represents a base game object. Contains common properties and a TexturedModel's to render.
 */
public abstract class Entity {

    private ArrayList<TexturedModel> texturedModels;
    private Vector3f position;
    private Matrix4f matrix;
    private Vector2f scale;
    private float rotation;

    /**
     * Creates a new Entity with the specified properties.
     * @param position the initial position of this Entity.
     * @param rotation the initial rotation of this Entity.
     * @param scale the initial scale of this Entity.
     * @param texturedModels TexturedModel's that this Entity should contain. Variable amount.
     */
    public Entity(Vector3f position, float rotation, float scale, TexturedModel... texturedModels){
        this.position = position;
        this.rotation = rotation;
        this.scale = new Vector2f(scale);
        this.texturedModels = new ArrayList<>();
        this.texturedModels.addAll(Arrays.asList(texturedModels));
        this.texturedModels.forEach((texturedModel) -> texturedModel.getAABB().setScale(this.scale));
    }

    /**
     * Sets the position of this object.
     * @param position the position, represented with a vector from the world origin.
     */
    public void setPosition(Vector3f position){
        this.position = position;
    }

    /**
     * Adds a vector to the position of this object.
     * @param position the vector to add to the position.
     */
    public void addPosition(Vector3f position){
        this.position.addThis(position);
    }

    /**
     * Sets the rotation around the z-axis of this object.
     * @param rotation the rotation, in degrees.
     */
    public void setRotation(float rotation){
        this.rotation = rotation;
    }

    /**
     * Sets the scale of this object.
     * @param scale the scale multiplier.
     */
    public void setScale(Vector2f scale){
        this.scale = scale;
    }

    /**
     * Updates the Model-View-Projection matrix with the current attribute values.
     */
    void updateMatrix(){
        this.matrix = new Matrix4f().MVP(this.position, this.rotation, this.scale);
    }

    /**
     * Gets the float array form of the matrix of this Entity.
     * @return the matrix in float array form.
     */
    public float[] getMatrix(){
        return this.matrix.matrix;
    }

    /**
     * Gets the position vector of this object.
     * @return the position vector.
     */
    public Vector3f getPosition(){
        return this.position;
    }

    /**
     * Gets the scale of this Entity.
     * @return the scale.
     */
    public Vector2f getScale(){
        return this.scale;
    }

    /**
     * Gets the TexturedModels.
     * @return a HashMap<Integer, TexturedModel> with the TexturedModel's
     */
    public ArrayList<TexturedModel> getTexturedModels(){
        return this.texturedModels;
    }

    /**
     * Gets the width of the default bouding box.
     * @return the width of the bounding box.
     */
    public float getWidth(){
        return getTexturedModels().get(0).getAABB().getWidth();
    }

    /**
     * Gets the height of the default bouding box.
     * @return the height of the bounding box.
     */
    public float getHeight(){
        return getTexturedModels().get(0).getAABB().getHeight();
    }

    /**
     * Gets the middle of the default bouding box.
     * @return the middle of the bounding box.
     */
    Vector3f getMiddle() {
        return getTexturedModels().get(0).getAABB().getMiddle();
    }

    /**
     * Cleans up every TexturedModel that this Entity has.
     */
    void cleanUp(){
        this.texturedModels.forEach(TexturedModel::cleanUp);
    }
}
