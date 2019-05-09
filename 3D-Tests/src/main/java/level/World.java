package main.java.level;

import main.java.debug.Line;
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

    /**
     * Constructor with the position of the Player in the same Level as this World.
     * @param playerPosition the position of the Player. Used to connect debug lines.
     */
    World(Vector3f playerPosition){
        this(new Vector3f(0.0f, -1.0f), 0.0f, 1.0f, playerPosition);
    }

    /**
     * Constructor with the initial position of this World, as well as the position of the Player in the same Level as this World.
     * @param position the initial position of this World.
     * @param playerPosition the position of the Player. Used to connect debug lines.
     */
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
            this.debugLines.add(new Line(texturedModel.getAABB().getMiddle(), texturedModel.getRelativePosition(), super.getPosition(), playerPosition));
        }
    }

    /**
     * Updates the world matrix and also the debug lines. !!SHOULD PROBABLY BE A COMMON PROPERTY OF ENTITY!!
     * @param playerPosition the position of the Player that is in the same Level as this World.
     */
    void updateMatrix(Vector3f playerPosition) {
        super.updateMatrix();
        for (Line line : this.debugLines){
            line.setOtherVertex(playerPosition);
            line.updateMatrix();
        }
    }

    /**
     * Gets the debug lines of this World.
     * @return the debug lines.
     */
    public ArrayList<Line> getDebugLines(){
        return this.debugLines;
    }
}