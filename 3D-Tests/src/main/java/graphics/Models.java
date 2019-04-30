package main.java.graphics;
//TODO Add JavaDoc
public class Models {

    private static final float grassLength = 20.0f, grassDepth = 2.0f;
    private static final float dirtLength = 20.0f, dirtDepth = 20.0f;

    public static final TexturedModel OBSTACLE = new TexturedModel("src/main/java/shaders/square_shader", "src/main/resources/pixel_test.png");
    public static final TexturedModel PLAYER = new TexturedModel("src/main/java/shaders/player_shader", "src/main/resources/player.png");
    private static TexturedModel GRASS_LAYER = null;
    private static TexturedModel DIRT_LAYER = null;

    public Models(){
        initGrassLayer();
        initDirtLayer();
    }

    private void initGrassLayer(){
        float[] vertices = {
                grassLength, -1.0f,
                grassLength, -grassDepth - 1.0f,
                0.0f,        -1.0f,
                0.0f,        -grassDepth - 1.0f
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
            GRASS_LAYER = new TexturedModel(vertices, indices, texcoords, "src/main/java/shaders/square_shader", "src/main/resources/grass.png");
        }else{
            System.out.println("[WARNING] Tried to initialize models more than once!");
        }
    }

    private void initDirtLayer(){
        float[] vertices = {
                dirtLength, -dirtDepth - grassDepth - 1.0f,
                dirtLength, -1.0f,
                0.0f,       -dirtDepth - grassDepth - 1.0f,
                0.0f,       -1.0f
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
            DIRT_LAYER = new TexturedModel(vertices, indices, texcoords, "src/main/java/shaders/square_shader", "src/main/resources/dirt.png");
        }else{
            System.out.println("[WARNING] Tried to initialize models more than once!");
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
