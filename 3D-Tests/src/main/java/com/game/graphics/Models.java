package com.game.graphics;

import com.game.math.Vector2f;
import com.game.math.Vector3f;

import java.util.HashMap;

/**
 * Container for all the TexturedModel's in the game.
 */
public class Models {

    static final Texture SPRITE_SHEET = new Texture("textures/sprite_sheet.png", true);
    private static final float SPRITE_SHEET_SIZE = SPRITE_SHEET.getWidth();

    public static final TexturedModel DIRT_TILE = new TexturedModel(SPRITE_SHEET, createTextureCoordinates(new Vector2f(), 16, 16));
    public static final TexturedModel GRASS_TILE = new TexturedModel(SPRITE_SHEET, createTextureCoordinates(new Vector2f(16.0f, 0.0f), 16, 16));
    public static final TexturedModel GRASS_SNOW_TILE = new TexturedModel(SPRITE_SHEET, createTextureCoordinates(new Vector2f(32.0f, 0.0f), 16, 16));
    public static final TexturedModel STONE_TILE = new TexturedModel(SPRITE_SHEET, createTextureCoordinates(new Vector2f(48.0f, 0.0f), 16, 16));

    public static final TexturedModel PLAYER = initModel(2.86f, new Vector2f(16.0f, 16.0f), 14, 40);
    public static final TexturedModel TREE = initModel(3.0f, new Vector2f(0.0f, 16.0f), 16, 48);

    private static float[] createTextureCoordinates(Vector2f pixelOrigin, int pixelWidth, int pixelHeight){
        return new float[]{
                (pixelOrigin.getX() + pixelWidth) / SPRITE_SHEET_SIZE,  pixelOrigin.getY() / SPRITE_SHEET_SIZE,
                (pixelOrigin.getX() + pixelWidth) / SPRITE_SHEET_SIZE, (pixelOrigin.getY() + pixelHeight) / SPRITE_SHEET_SIZE,
                 pixelOrigin.getX() / SPRITE_SHEET_SIZE,                pixelOrigin.getY() / SPRITE_SHEET_SIZE,
                 pixelOrigin.getX() / SPRITE_SHEET_SIZE,               (pixelOrigin.getY() + pixelHeight) / SPRITE_SHEET_SIZE
        };
    }

    /**
     * Creates the vertex and texture coordinate data for a rectangle with specified width height and relative position within the parent entity.
     * @param relativePosition the relative position of the parent entity.
     * @param height the height of the model.
     * @return a HashMap of string keys and the corresponding data for easily seeing what data is what. (Rather than indices in an array).
     */
    private static HashMap<String, float[]> createVertexData(Vector3f relativePosition, float height, Vector2f pixelOrigin, int pixelWidth, int pixelHeight){
        float[] vertices = {
            relativePosition.getX() + 1.0f,     relativePosition.getY(),
            relativePosition.getX() + 1.0f,     relativePosition.getY() - height,
            relativePosition.getX(),             relativePosition.getY(),
            relativePosition.getX(),             relativePosition.getY() - height
        };
        HashMap<String, float[]> vertexData = new HashMap<>();
        vertexData.put("vertices", vertices);
        vertexData.put("texcoords", createTextureCoordinates(pixelOrigin, pixelWidth, pixelHeight));
        return vertexData;
    }

    private static TexturedModel initModel(float height, Vector2f pixelOrigin, int pixelWidth, int pixelHeight){
        HashMap<String, float[]> vertexData = createVertexData(new Vector3f(), height, pixelOrigin, pixelWidth, pixelHeight);
        return new TexturedModel(vertexData.get("vertices"), vertexData.get("texcoords"), new Vector3f(), 1.0f, height, SPRITE_SHEET);
    }
}
