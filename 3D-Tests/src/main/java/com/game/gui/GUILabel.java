package com.game.gui;

import com.game.graphics.font.Font;
import com.game.math.Vector3f;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public class GUILabel extends GUIComponent
{
    private Font font;
    private Font.TextRenderData textRenderData;
    private int fontBitmapID;

    public GUILabel(Vector3f position, Font font)
    {
        this(position, font, "");
    }

    public GUILabel(Vector3f position, Font font, String text)
    {
        super(position);
        this.font = font;
        fontBitmapID = font.getFontBitmapID();
        setText(text);
    }

    public void setText(String text)
    {
        textRenderData = font.createTextRenderData(text);
    }

    public void bind()
    {
        glBindTexture(GL_TEXTURE_2D, fontBitmapID);
    }

    public Font getFont()
    {
        return font;
    }

    @Override
    public Font.TextRenderData getTextRenderData()
    {
        return textRenderData;
    }
}
