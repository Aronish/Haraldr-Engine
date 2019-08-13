package com.game.level;

import com.game.level.gameobject.GameObject;
import com.game.math.Matrix4f;
import com.game.math.Vector2f;
import com.game.math.Vector3f;

/**
 * Class that represents a base game object. Contains common properties and a type that holds render data.
 */
public abstract class Entity {

    private GameObject gameObjectType;

    private Vector3f position;
    private float rotation;
    private Vector2f scale;
    private Matrix4f matrix;

    /**
     * Creates a new Entity with the specified properties.
     * @param position the initial position of this Entity.
     * @param rotation the initial rotation of this Entity.
     * @param scale the initial scale of this Entity.
     * @param gameObjectType the type of game object that this Entity will be.
     */
    public Entity(Vector3f position, float rotation, float scale, GameObject gameObjectType){
        this.position = position;
        this.rotation = rotation;
        this.scale = new Vector2f(scale);
        this.gameObjectType = gameObjectType;
        updateMatrix();
    }

    /**
     * Sets the position of this Entity.
     * @param position the position, represented with a vector from the world origin.
     */
    public void setPosition(Vector3f position){
        this.position = position;
    }

    /**
     * Adds a vector to the position of this Entity.
     * @param position the vector to add to the position.
     */
    public void addPosition(Vector3f position){
        this.position.addThis(position);
    }
    /*
    /**
     * Sets the rotation around the z-axis of this Entity.
     * @param rotation the rotation, in degrees.
     *//*
    public void setRotation(float rotation){
        this.rotation = rotation;
    }
    */
    /**
     * Sets the scale of this Entity.
     * @param scale the scale multiplier.
     */
    public void setScale(Vector2f scale){
        this.scale = scale;
    }

    /**
     * Updates the model matrix with the current attribute values.
     */
    void updateMatrix(){
        matrix = Matrix4f.transform(this.position, this.rotation, this.scale, false);
    }

    /**
     * @return the model matrix in float array form.
     */
    public float[] getMatrixArray(){
        return matrix.matrix;
    }

    /**
     * @return the position vector.
     */
    public Vector3f getPosition(){
        return position;
    }

    /**
     * @return the scale.
     */
    public Vector2f getScale(){
        return scale;
    }

    /**
     * @return the game object type.
     */
    public GameObject getGameObjectType(){
        return gameObjectType;
    }
}
