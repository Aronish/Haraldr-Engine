package main.java.level;

import main.java.graphics.Models;
import main.java.math.Vector3f;

public class Tree extends Entity{

    public Tree(){
        this(new Vector3f(), 0.0f, 1.0f);
    }

    Tree(Vector3f position){
        this(position, 0.0f, 1.0f);
    }

    private Tree(Vector3f position, float rotation, float scale) {
        super(position, rotation, scale, Models.getTREE());
    }
}
