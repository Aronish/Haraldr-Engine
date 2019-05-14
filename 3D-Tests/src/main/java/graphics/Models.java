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

    private static final float grassLength = 20.0f, grassDepth = 2.0f;
    private static final float dirtLength = 20.0f, dirtDepth = 10.0f;

    public static final TexturedModel OBSTACLE = new TexturedModel("src/main/java/shaders/square_shader", "src/main/resources/pixel_test.png");
    public static final TexturedModel PLAYER = new TexturedModel("src/main/java/shaders/player_shader", "src/main/resources/player.png");
    private static TexturedModel GRASS_LAYER = null;
    private static TexturedModel DIRT_LAYER = null;

    /**
     * Initializes the non-final models.
     */
    public Models(){
        initGrassLayer(new Vector3f(), grassLength, grassDepth);
        initDirtLayer(new Vector3f(10.0f, -grassDepth), dirtLength, dirtDepth);
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
    private static void initGrassLayer(Vector3f relativePosition, float width, float height){
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
    private static void initDirtLayer(Vector3f relativePosition, float width, float height){
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
     * @return the grass layer model.
     */
    public static TexturedModel getGRASS_LAYER(){
        if(GRASS_LAYER != null){
            return GRASS_LAYER;
        }else{
            Logger.setErrorLevel();
            Logger.log("Models not initialized!");
            throw new RuntimeException("Models are never initialized!");
        }
    }

    /**
     * Gets the dirt layer model with a security check for null.
     * @return the dirt layer model.
     */
    public static TexturedModel getDIRT_LAYER() {
        if (DIRT_LAYER != null) {
            return DIRT_LAYER;
        } else {
            Logger.setErrorLevel();
            Logger.log("Models not initialized!");
            throw new RuntimeException("Models are never initialized!");
        }
    }
}
