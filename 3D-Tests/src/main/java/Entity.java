package main.java;

import main.java.graphics.TexturedModel;
import main.java.math.Matrix4f;
import main.java.math.Vector3f;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

/**
 * Class that represents a base game object. Contains common properties and a textured model to render.
 */
public class Entity {

    private TexturedModel texturedModel;
    Vector3f position;
    private float rotation;
    private float scale;
    private Matrix4f matrix;
    private int matrixLocation;

    /**
     * Constructor without rotation and scale.
     * @param texturedModel the textured model for this entity to render.
     * @param position the initial position of this entity.
     */
    public Entity(TexturedModel texturedModel, Vector3f position){
        this(texturedModel, position, 0.0f, 1.0f);
    }

    /**
     * Constructor with rotation and scale as well.
     * @param texturedModel the textured model for the entity to render.
     * @param position the initial position of this entity.
     * @param rotation the initial rotation of this entity.
     * @param scale the initial scale of this entity.
     */
    public Entity(TexturedModel texturedModel, Vector3f position, float rotation, float scale){
        this.texturedModel = texturedModel;
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
    public void updateMatrix(){
        this.matrix = new Matrix4f().MVP(this.position, this.rotation, this.scale);
    }

    /**
     * Gets the matrix location in the shader from OpenGL and stores the ID for later use.
     */
    void setMatrixLocation(){
        this.matrixLocation = glGetUniformLocation(this.texturedModel.getShader().getShaderProgram(), "matrix");
    }

    /**
     * Sets the uniform variable in the vertex shader to the current Model-View-Projection matrix.
     */
    public void setUniformMatrix(){
        glUniformMatrix4fv(this.matrixLocation, false, this.matrix.matrix);
    }

    /**
     * Gets the position vector of this object.
     * @return the position vector.
     */
    Vector3f getPosition(){
        return new Vector3f(this.position.x, this.position.y, this.position.z);
    }

    /**
     * Gets the real width with scale compensation.
     * @return the real width.
     */
    float getWidth(){
        return this.texturedModel.getVertexArray().getWidth() * this.scale;
    }

    /**
     * Gets the real height with scale compensation.
     * @return the real height.
     */
    float getHeight(){
        return this.texturedModel.getVertexArray().getHeight() * this.scale;
    }

    /**
     * Gets the TexturedModel.
     * @return the TexturedModel.
     */
    public TexturedModel getTexturedModel(){
        return this.texturedModel;
    }
}
