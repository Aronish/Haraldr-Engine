package com.game.graphics;

import com.game.gameobject.GameObject;
import com.game.math.Vector2f;

import java.util.HashMap;

/**
 * Container for the sprite sheets and all the Models in the game.
 */
public class Models {

    private static HashMap<String, float[]> vertexData = new HashMap<>();

    static final Texture SPRITE_SHEET = new Texture("textures/sprite_sheet.png");
    private static final float SPRITE_SHEET_SIZE = SPRITE_SHEET.getWidth();

    public static final Model DIRT_TILE = new Model(createTextureCoordinates(new Vector2f(), 16, 16));
    public static final Model GRASS_TILE = new Model(createTextureCoordinates(new Vector2f(16.0f, 0.0f), 16, 16));
    public static final Model GRASS_SNOW_TILE = new Model(createTextureCoordinates(new Vector2f(32.0f, 0.0f), 16, 16));
    public static final Model STONE_TILE = new Model(createTextureCoordinates(new Vector2f(48.0f, 0.0f), 16, 16));

    public static final Model PLAYER = initModel(2.86f, new Vector2f(16.0f, 16.0f), 14, 40);
    public static final Model TREE = initModel(3.0f, new Vector2f(0.0f, 16.0f), 16, 48);


    /**
     * Helper method that creates an array of texture coordinates from the provided information.
     * ((0, 0) is apparently in the upper left corner in my case. (It's usually in the lower left.))
     * @param pixelOrigin the upper left corner of the sprite in the sprite sheet.
     * @param pixelWidth the width of the sprite in pixels.
     * @param pixelHeight the height of the sprite in pixels.
     * @return the array of texture coordinates.
     */
    private static float[] createTextureCoordinates(Vector2f pixelOrigin, int pixelWidth, int pixelHeight){
        return new float[]{
                (pixelOrigin.getX() + pixelWidth) / SPRITE_SHEET_SIZE,  pixelOrigin.getY() / SPRITE_SHEET_SIZE,
                (pixelOrigin.getX() + pixelWidth) / SPRITE_SHEET_SIZE, (pixelOrigin.getY() + pixelHeight) / SPRITE_SHEET_SIZE,
                 pixelOrigin.getX() / SPRITE_SHEET_SIZE,                pixelOrigin.getY() / SPRITE_SHEET_SIZE,
                 pixelOrigin.getX() / SPRITE_SHEET_SIZE,               (pixelOrigin.getY() + pixelHeight) / SPRITE_SHEET_SIZE
        };
    }

    /**
     * Creates the vertex data for a rectangle with specified height.
     * (Does not support width ATM, should probably fix.)
     * @param height the height of the model.
     * @return a HashMap of string keys and the corresponding data for easily seeing what data is what. (Rather than indices in an array).
     */
    private static HashMap<String, float[]> createVertexData(float height, Vector2f pixelOrigin, int pixelWidth, int pixelHeight){
        float[] vertices = {
            1.0f,   0.0f,
            1.0f,   0.0f - height,
            0.0f,   0.0f,
            0.0f,   0.0f - height
        };
        vertexData.clear();
        vertexData.put("vertices", vertices);
        vertexData.put("texcoords", createTextureCoordinates(pixelOrigin, pixelWidth, pixelHeight));
        return vertexData;
    }

    /**
     * Initializes a Model with vertex and texture coordinate data.
     * @param height the height of the Model in units.
     * @param pixelOrigin the upper left corner of the sprite in the sprite sheet.
     * @param pixelWidth the width of the sprite in pixels.
     * @param pixelHeight the height of the sprite in pixels.
     * @return the newly constructed Model.
     */
    private static Model initModel(float height, Vector2f pixelOrigin, int pixelWidth, int pixelHeight){
        HashMap<String, float[]> vertexData = createVertexData(height, pixelOrigin, pixelWidth, pixelHeight);
        return new Model(vertexData.get("vertices"), vertexData.get("texcoords"), 1.0f, height);
    }

    public static void cleanUp(){
        SPRITE_SHEET.delete();
        for (GameObject gameObjectType : GameObject.values()){
            gameObjectType.model.cleanUp();
        }
    }
}
