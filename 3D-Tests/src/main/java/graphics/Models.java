package main.java.graphics;

import main.java.Logger;

/**
 * Container for all the TexturedModel's in the game.
 */
public class Models {

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
        initGrassLayer();
        initDirtLayer();
    }

    /**
     * Initializes the grass layer model.
     */
    private void initGrassLayer(){
        float[] vertices = {
                grassLength, 0.0f,
                grassLength, -grassDepth,
                0.0f,        0.0f,
                0.0f,        -grassDepth
        };
        int[] indices = {
                0, 1, 2,
                1, 3, 2
        };
        float[] texcoords = {
                grassLength, 0.0f,
                grassLength, grassDepth,
                0.0f,        0.0f,
                0.0f,        grassDepth
        };
        if (GRASS_LAYER == null){
            GRASS_LAYER = new TexturedModel(vertices, indices, texcoords, grassLength, grassDepth, "src/main/java/shaders/square_shader", "src/main/resources/grass.png");
        }else{
            Logger.setWarningLevel();
            Logger.log("Tried to initialize the models more than once!");
        }
    }

    /**
     * Initializes the dirt layer model. Is always below the grass layer.
     */
    private void initDirtLayer(){
        float[] vertices = {
                dirtLength, -dirtDepth - grassDepth,
                dirtLength, 0.0f,
                0.0f,       -dirtDepth - grassDepth,
                0.0f,       0.0f
        };
        int[] indices = {
                0, 1, 2,
                1, 3, 2
        };
        float[] texcoords = {
                dirtLength, 0.0f,
                dirtLength, dirtDepth + grassDepth,
                0.0f,       0.0f,
                0.0f,       dirtDepth + grassDepth
        };
        if (DIRT_LAYER == null){
            DIRT_LAYER = new TexturedModel(vertices, indices, texcoords, dirtLength, dirtDepth, "src/main/java/shaders/square_shader", "src/main/resources/dirt.png");
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
