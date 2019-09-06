package com.game.gui;

import com.game.graphics.font.Font;
import com.game.math.Vector3f;

public class GUILabel extends GUITextComponent
{
    private Font font;
    private Font.TextRenderData textRenderData;

    public GUILabel(Vector3f position, Font font)
    {
        this(position, 0.01f, font, "");
    }

    public GUILabel(Vector3f position, float scale, Font font)
    {
        this(position, scale, font, "");
    }

    public GUILabel(Vector3f position, float scale, Font font, String text)
    {
        super(position, scale);
        this.font = font;
        setText(text);
    }

    public void setText(String text)
    {
        textRenderData = font.createTextRenderData(text);
    }

    @Override
    public Font.TextRenderData getTextRenderData()
    {
        return textRenderData;
    }
}
