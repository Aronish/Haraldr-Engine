package com.game.graphics;

import com.game.gameobject.GameObject;

/**
 * Container for the sprite sheets and all the Models in the game. Handles most disposing.
 * TODO: Read model files and construct automatically.
 */
public class Models
{
    static final Texture SPRITE_SHEET = new Texture("textures/sprite_sheet_2.png", false);
    private static final float SPRITE_SHEET_SIZE = SPRITE_SHEET.getWidth();
    private static final float SPRITE_SIZE = 16;

    //IMPORTANT! To avoid texture atlas bleeding, textures need duplicate edges so that filtering won't use colors from neighboring textures.
    public static final Model DIRT_TILE =           initModelImpr(1, 1, 16, 16);
    public static final Model GRASS_TILE =          initModelImpr(19, 1, 16, 16);
    public static final Model GRASS_SNOW_TILE =     initModelImpr(37, 1, 16, 16);
    public static final Model STONE_TILE =          initModelImpr(55, 1, 16, 16);

    public static final Model GRASS_TUFT =          initModelImpr(73, 14, 16, 4);
    public static final Model TREE =                initModelImpr(1, 19, 16, 48);

    public static final Model PLAYER =              initModelImpr(19, 19, 14, 40);

    //                                                                           FOR TEXTURE COORDINATES
    private static float[] createVertexData(float modelWidth, float modelHeight, int x, int y, int width, int height)
    {
        return new float[]
        {
                0.0f,       0.0f,                x          / SPRITE_SHEET_SIZE,    y           / SPRITE_SHEET_SIZE, //TOP LEFT
                modelWidth, 0.0f,               (x + width) / SPRITE_SHEET_SIZE,    y           / SPRITE_SHEET_SIZE, //TOP RIGHT
                modelWidth, 0.0f - modelHeight, (x + width) / SPRITE_SHEET_SIZE,   (y + height) / SPRITE_SHEET_SIZE, //BOTTOM RIGHT
                0.0f,       0.0f - modelHeight,  x          / SPRITE_SHEET_SIZE,   (y + height) / SPRITE_SHEET_SIZE  //BOTTOM LEFT
        };
    }

    private static Model initModelImpr(int x, int y, int spriteWidth, int spriteHeight)
    {
        float modelWidth = spriteWidth / SPRITE_SIZE;
        float modelHeight = spriteHeight / SPRITE_SIZE;
        VertexBufferLayout layout = new VertexBufferLayout
        (
                new VertexBufferElement(ShaderDataType.FLOAT2),
                new VertexBufferElement(ShaderDataType.FLOAT2)
        );
        return new Model
        (
                createVertexData(modelWidth, modelHeight, x, y, spriteWidth, spriteHeight),
                layout, modelWidth, modelHeight
        );
    }

    public static void dispose()
    {
        SPRITE_SHEET.unbind();
        SPRITE_SHEET.delete();
        for (GameObject gameObjectType : GameObject.values())
        {
            gameObjectType.getModel().dispose();
        }
    }
}
