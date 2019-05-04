package main.java;

import main.java.graphics.Models;
import main.java.math.Vector3f;

public class Line extends Entity {

    private Vector3f otherVertex;

    Line(){
        this(new Vector3f(), new Vector3f());
    }

    Line(Vector3f position, Vector3f otherVertex){
        super(position, 0.0f, 1.0f, Models.getLINE());
        //setOtherVertex(otherVertex);
    }

    private void setOtherVertex(Vector3f otherVertex){
        this.otherVertex = otherVertex;
        updateVertexData();
    }

    private void updateVertexData(){
        getTexturedModels().get(0).getVertexArray().updateVertexData(new float[] {this.otherVertex.x, this.otherVertex.y});
    }
}
