package com.game.gameobject;

import com.game.math.Matrix4f;
import com.game.math.Vector2f;
import com.game.math.Vector3f;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Class that represents a base game object. Contains common properties and a type that holds render data.
 */
public abstract class Entity implements Serializable {

    protected GameObject gameObjectType;

    protected Vector3f position;
    protected transient Vector2f scale;
    private transient float rotation;
    private Matrix4f matrix;

    private static transient int serializeCount = 0;

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

    public void serialize(){
        try{
            FileOutputStream ostream = new FileOutputStream("src/main/resources/ser/temp" + serializeCount + ".ser");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(ostream);
            objectOutputStream.writeObject(this);
            objectOutputStream.close();
            ostream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        ++serializeCount;
    };
}
