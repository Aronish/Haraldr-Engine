package main.java;

import main.java.graphics.Models;
import main.java.math.Vector3f;

class Obstacle extends Entity {

    Obstacle(){
        this(new Vector3f(), 0.0f, 1.0f);
    }

    Obstacle(Vector3f position){
        this(position, 0.0f, 1.0f);
    }

    private Obstacle(Vector3f position, float rotation, float scale){
        super(Models.OBSTACLE, position, rotation, scale);
        this.setMatrixLocation();
    }
}
