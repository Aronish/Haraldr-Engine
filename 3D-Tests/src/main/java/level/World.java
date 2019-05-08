package main.java.level;

import main.java.Line;
import main.java.graphics.Models;
import main.java.graphics.TexturedModel;
import main.java.math.Vector3f;

import java.util.ArrayList;

/**
 * Main world object, on which everything should be located to be inside the world.
 * For future development style, this class should manage everything to do with the world in a level.
 */
public class World extends Entity {

    private ArrayList<Line> debugLines;

    World(Vector3f playerPosition){
        this(new Vector3f(0.0f, -1.0f), 0.0f, 1.0f, playerPosition);
    }

    World(Vector3f position, Vector3f playerPosition){
        this(position, 0.0f, 1.0f, playerPosition);
    }

    /**
     * Constructor with parameters for position, rotation and scale.
     * @param position the position of the object. An origin vector. Bottom left corner.
     * @param rotation the rotation around the z-axis, in degrees.
     * @param scale the scale multiplier of this object.
     */
    private World(Vector3f position, float rotation, float scale, Vector3f playerPosition){
        super(position, rotation, scale, Models.getDIRT_LAYER(), Models.getGRASS_LAYER());
        this.debugLines = new ArrayList<>();
        for (TexturedModel texturedModel : super.getTexturedModels().values()){
            this.debugLines.add(new Line(texturedModel.getAABB().getMiddle(), texturedModel.getRelativePosition(), playerPosition));
        }
    }

    void updateMatrix(Vector3f playerPosition) {
        super.updateMatrix();
        for (Line line : this.debugLines){
            line.setOtherVertex(playerPosition);
            line.updateMatrix();
        }
    }

    public ArrayList<Line> getDebugLines(){
        return this.debugLines;
    }
}