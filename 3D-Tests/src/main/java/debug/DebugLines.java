package main.java.debug;

import main.java.graphics.Renderer;
import main.java.graphics.TexturedModel;
import main.java.level.Entity;
import main.java.math.Vector3f;

import java.util.ArrayList;

/**
 * Container class for debug lines.
 */
public class DebugLines {

    private ArrayList<Line> debugLines;

    /**
     * Initializes the ArrayList containing all the debug lines.
     */
    public DebugLines(){
        this.debugLines = new ArrayList<>();
    }

    /**
     * Adds debug lines to the middle of every TexturedModel in the provided Entity.
     * @param entity the Entity whose TexturedModels should have debug lines.
     */
    public void addDebugLines(Entity entity){
        for (TexturedModel texturedModel : entity.getTexturedModels()){
            this.debugLines.add(new Line(texturedModel.getAABB().getMiddle(), texturedModel.getRelativePosition(), entity.getPosition()));
        }
    }

    /**
     * Sets the second vertex of every line to the Player's position.
     * @param playerPosition the position of the Player to connect to.
     */
    public void setPlayerEnd(Vector3f playerPosition){
        for (Line line : this.debugLines){
            line.setOtherVertex(playerPosition);
        }
    }

    /**
     * Updates the matrices for every line.
     */
    public void update(){
        for (Line line : this.debugLines){
            line.updateMatrix();
        }
    }

    /**
     * Renders every line.
     */
    public void render(){
        for (Line line : this.debugLines){
            Renderer.render(line);
        }
    }

    public void cleanUp(){
        this.debugLines.forEach(Line::cleanUp);
    }
}
