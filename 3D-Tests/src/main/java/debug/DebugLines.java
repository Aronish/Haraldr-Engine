package main.java.debug;
//TODO ADD JAVADOC
import main.java.graphics.Renderer;
import main.java.graphics.TexturedModel;
import main.java.level.Entity;
import main.java.math.Vector3f;

import java.util.ArrayList;

public class DebugLines {

    private ArrayList<Line> debugLines;

    public DebugLines(){
        this.debugLines = new ArrayList<>();
    }

    public void addDebugLines(Entity entity){
        for (TexturedModel texturedModel : entity.getTexturedModels()){
            this.debugLines.add(new Line(texturedModel.getAABB().getMiddle(), texturedModel.getRelativePosition(), entity.getPosition()));
        }
    }

    public void setPlayerEnd(Vector3f playerPosition){
        for (Line line : this.debugLines){
            line.setOtherVertex(playerPosition);
        }
    }

    public void update(){
        for (Line line : this.debugLines){
            line.updateMatrix();
        }
    }

    public void render(){
        for (Line line : this.debugLines){
            Renderer.render(line);
        }
    }
}
