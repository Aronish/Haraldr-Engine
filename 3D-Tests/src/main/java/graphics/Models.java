package main.java.graphics;
//TODO Improve creation of vertex data (length, depth, relative coords...).
import main.java.Logger;
import main.java.math.Vector3f;

import java.util.HashMap;

/**
 * Container for all the TexturedModel's in the game.
 */
public class Models {

    private int[] defIndices = {
            0, 1, 2,
            1, 3, 2
    };

    private static final float grassLength = 50.0f, grassDepth = 2.0f;
    private static final float dirtLength = 50.0f, dirtDepth = 20.0f;

    public static final TexturedModel OBSTACLE = new TexturedModel("src/main/java/shaders/square_shader", "src/main/resources/pixel_test.png");
    public static final TexturedModel PLAYER = new TexturedModel("src/main/java/shaders/player_shader", "src/main/resources/player.png");
    private static TexturedModel GRASS_LAYER = null;
    private static TexturedModel DIRT_LAYER = null;

    /**
     * Initializes the non-final models.
     */
    public Models(){
        initGrassLayer(new Vector3f(), grassLength, grassDepth);
        initDirtLayer(new Vector3f(0.0f, -grassDepth), dirtLength, dirtDepth);
    }

    private HashMap<String, float[]> createVertexData(Vector3f relativePosition, float width, float height){
        float[] vertices = {
            relativePosition.x + width,     relativePosition.y,
            relativePosition.x + width,     relativePosition.y - height,
            relativePosition.x,             relativePosition.y,
            relativePosition.x,             relativePosition.y - height
        };
        float[] texcoords = {
            width,  0.0f,
            width,  height,
            0.0f,   0.0f,
            0.0f,   height
        };
        HashMap<String, float[]> vertexData = new HashMap<>();
        vertexData.put("vertices", vertices);
        vertexData.put("texcoords", texcoords);
        return vertexData;
    }

    /**
     * Initializes the grass layer model.
     */
    private void initGrassLayer(Vector3f relativePosition, float width, float height){
        HashMap<String, float[]> vertexData = createVertexData(relativePosition, width, height);
        if (GRASS_LAYER == null){
            GRASS_LAYER = new TexturedModel(vertexData.get("vertices"), defIndices, vertexData.get("texcoords"), relativePosition, width, height, "src/main/java/shaders/square_shader", "src/main/resources/grass.png");
        }else{
            Logger.setWarningLevel();
            Logger.log("Tried to initialize the models more than once!");
        }
    }

    /**
     * Initializes the dirt layer model. Is always below the grass layer.
     */
    private void initDirtLayer(Vector3f relativePosition, float width, float height){
        HashMap<String, float[]> vertexData = createVertexData(relativePosition, width, height);
        if (DIRT_LAYER == null){
            DIRT_LAYER = new TexturedModel(vertexData.get("vertices"), defIndices, vertexData.get("texcoords"), relativePosition, width, height, "src/main/java/shaders/square_shader", "src/main/resources/dirt.png");
        }else{
            Logger.setWarningLevel();
            Logger.log("Tried to initialize the models more than once!");
        }
    }

    /**
     * Gets the grass layer model with a security check for null.
     * @return the world model.
     */
    public static TexturedModel getGRASS_LAYER(){
        if(GRASS_LAYER != null){
            return GRASS_LAYER;
        }else{
            throw new RuntimeException("[FATAL ERROR] Models not initialized!");
        }
    }

    /**
     * Gets the dirt layer model with a security check for null.
     * @return the world model.
     */
    public static TexturedModel getDIRT_LAYER(){
        if(DIRT_LAYER != null){
            return DIRT_LAYER;
        }else{
            throw new RuntimeException("[FATAL ERROR] Models not initialized!");
        }
    }
}
