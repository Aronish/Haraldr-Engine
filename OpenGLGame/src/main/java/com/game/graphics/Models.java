package com.game.graphics;

import com.game.gameobject.GameObject;

/**
 * Container for the sprite sheets and all the Models in the game.
 */
public class Models {

    static final Texture SPRITE_SHEET = new Texture("textures/sprite_sheet_2.png");
    private static final float SPRITE_SHEET_SIZE = SPRITE_SHEET.getWidth();
    private static final float SPRITE_SIZE = 16;

    //IMPORTANT! To avoid texture atlas bleeding, textures need duplicate edges so that filtering won't use colors from neighboring textures.
    public static final Model DIRT_TILE =           new Model(createTextureCoordinates(1, 1, 16, 16));
    public static final Model GRASS_TILE =          new Model(createTextureCoordinates(19, 1, 16, 16));
    public static final Model GRASS_SNOW_TILE =     new Model(createTextureCoordinates(37, 1, 16, 16));
    public static final Model STONE_TILE =          new Model(createTextureCoordinates(55, 1, 16, 16));

    public static final Model GRASS_TUFT =          initModel(73, 14, 16, 4);
    public static final Model PLAYER =              initModel(19, 19, 14, 40);
    public static final Model TREE =                initModel(1, 19, 16, 48);

    /**
     * Helper method that creates an array of texture coordinates from the provided information.
     * ((0, 0) is apparently in the upper left corner in my case.
     * @param width the width of the sprite in pixels.
     * @param height the height of the sprite in pixels.
     */
    private static float[] createTextureCoordinates(int x, int y, int width, int height){
        return new float[]{
             x          / SPRITE_SHEET_SIZE,    y           / SPRITE_SHEET_SIZE,
            (x + width) / SPRITE_SHEET_SIZE,    y           / SPRITE_SHEET_SIZE,
            (x + width) / SPRITE_SHEET_SIZE,   (y + height) / SPRITE_SHEET_SIZE,
             x          / SPRITE_SHEET_SIZE,   (y + height) / SPRITE_SHEET_SIZE
        };
    }

    /**
     * Helper method that creates an array of vertex coordinates from the provided information.
     * @param modelWidth the width of the model in units.
     * @param modelHeight the height of the model in units.
     */
    private static float[] createVertices(float modelWidth, float modelHeight){
        return new float[] {
                0.0f,       0.0f,
                modelWidth, 0.0f,
                modelWidth, 0.0f - modelHeight,
                0.0f,       0.0f - modelHeight
        };
    }

    /**
     * Initializes a Model with vertex and texture coordinate data. This is for models that aren't 1x1 tiles.
     * @param x the top left x coordinate in the sprite sheet.
     * @param y the top left y coordinate in the sprite sheet.
     * @param spriteWidth the width of the sprite in pixels.
     * @param spriteHeight the height of the sprite in pixels.
     * @return the newly constructed Model.
     */
    private static Model initModel(int x, int y, int spriteWidth, int spriteHeight){
        float modelWidth = spriteWidth / SPRITE_SIZE;
        float modelHeight = spriteHeight / SPRITE_SIZE;
        return new Model(createVertices(modelWidth, modelHeight), createTextureCoordinates(x, y, spriteWidth, spriteHeight), modelWidth, modelHeight);
    }

    public static void cleanUp(){
        SPRITE_SHEET.delete();
        for (GameObject gameObjectType : GameObject.values()){
            gameObjectType.model.cleanUp();
        }
    }
}
