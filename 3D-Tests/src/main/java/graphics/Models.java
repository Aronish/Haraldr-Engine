package main.java.graphics;

public class Models {

    public static final TexturedModel OBSTACLE = new TexturedModel("src/main/java/shaders/square_shader", "src/main/resources/pixel_test.png");
    public static final TexturedModel PLAYER = new TexturedModel("src/main/java/shaders/player_shader", "src/main/resources/player.png");
    private static TexturedModel WORLD = null;

    public Models(float worldSize){
        initWorldModel(worldSize);
    }

    private void initWorldModel(float worldSize){
        float[] worldVertices = {
                1.0f * worldSize,   1.0f * worldSize,
                1.0f * worldSize,   0.0f,
                0.0f,               1.0f * worldSize,
                0.0f,               0.0f
        };
        int[] worldIndices = {
                0, 1, 2,
                1, 3, 2
        };
        float[] worldTexcoords = {
                1.0f * worldSize,   0.0f,
                1.0f * worldSize,   1.0f * worldSize,
                0.0f,               0.0f,
                0.0f,               1.0f * worldSize
        };
        if (WORLD == null){
            WORLD = new TexturedModel(worldVertices, worldIndices, worldTexcoords, "src/main/java/shaders/square_shader", "src/main/resources/grass.png");
        }else{
            System.out.println("[WARNING] Tried to initialize models more than once!");
        }
    }

    public static TexturedModel getWORLD(){
        if(WORLD != null){
            return WORLD;
        }else{
            throw new RuntimeException("[FATAL ERROR] Models not initialized!");
        }
    }
}
