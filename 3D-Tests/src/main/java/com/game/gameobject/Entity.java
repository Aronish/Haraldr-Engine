package com.game.gameobject;

import com.game.math.Matrix4f;
import com.game.math.Vector2f;
import com.game.math.Vector3f;

/**
 * Class that represents a base game object. Contains common properties and a type that holds render data.
 */
public abstract class Entity {

    protected GameObject gameObjectType;

    protected Vector3f position;
    protected Vector2f scale;
    private float rotation;
    private Matrix4f matrix;

    public Entity(Vector3f position, float rotation, float scale, GameObject gameObjectType){
        this.position = position;
        this.rotation = rotation;
        this.scale = new Vector2f(scale);
        this.gameObjectType = gameObjectType;
        updateMatrix();
    }

    public void setPosition(Vector3f position){
        this.position = position;
    }

    public void addPosition(Vector3f position){
        this.position.add(position);
    }

    public void setRotation(float rotation){
        this.rotation = rotation;
    }

    public void setScale(Vector2f scale){
        this.scale = scale;
    }

    public void updateMatrix(){
        matrix = Matrix4f.transform(this.position, this.rotation, this.scale, false);
    }

    public float[] getMatrixArray(){
        return matrix.matrix;
    }

    public Vector3f getPosition(){
        return position;
    }

    public GameObject getGameObjectType(){
        return gameObjectType;
    }
}
