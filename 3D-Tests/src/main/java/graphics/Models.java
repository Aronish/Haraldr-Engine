package main.java.graphics;

import main.java.debug.Logger;
import main.java.math.Vector3f;

import java.util.HashMap;

/**
 * Container for all the TexturedModel's in the game.
 */
public class Models {

    private static int[] defIndices = {
            0, 1, 2,
            1, 3, 2
    };

    public static final TexturedModel DEFAULT = new TexturedModel("src/main/java/shaders/square_shader", "src/main/resources/black.png");
    public static final TexturedModel GRASS_TILE = new TexturedModel("src/main/java/shaders/square_shader", "src/main/resources/grass.png");
    public static final TexturedModel DIRT_TILE = new TexturedModel("src/main/java/shaders/square_shader", "src/main/resources/dirt.png");

    private static TexturedModel PLAYER = null;

    public Models(){
        initPlayerModel(1.0f, 2.86f);
    }

    /**
     * Creates the vertex and texture coordinate data for a rectangle with specified width height and relative position within the parent entity.
     * @param relativePosition the relative position of the parent entity.
     * @param width the width of the model.
     * @param height the height of the model.
     * @return a HashMap of string keys and the corresponding data for easily seeing what data is what. (Rather than indices in an array).
     */
    private static HashMap<String, float[]> createVertexData(Vector3f relativePosition, float width, float height){
        float[] vertices = {
            relativePosition.x + width,     relativePosition.y,
            relativePosition.x + width,     relativePosition.y - height,
            relativePosition.x,             relativePosition.y,
            relativePosition.x,             relativePosition.y - height
        };
        float[] texcoords = {
            -1.0f,  0.0f,
            -1.0f,  1.0f,
            0.0f,   0.0f,
            0.0f,   1.0f
        };
        HashMap<String, float[]> vertexData = new HashMap<>();
        vertexData.put("vertices", vertices);
        vertexData.put("texcoords", texcoords);
        return vertexData;
    }

    private static void initPlayerModel(float width, float height){
        if (PLAYER == null){
            HashMap<String, float[]> vertexData = createVertexData(new Vector3f(), width, height);
            PLAYER = new TexturedModel(vertexData.get("vertices"), defIndices, vertexData.get("texcoords"), new Vector3f(), width, height, "src/main/java/shaders/player_shader", "src/main/resources/new_player.png");
        }else{
            Logger.setWarningLevel();
            Logger.log("Tried to initialize models more than once!");
        }
    }

    public static TexturedModel getPLAYER(){
        if (PLAYER != null){
            return PLAYER;
        }else{
            throw new RuntimeException("Models were not itialized");
        }
    }
}
