package com.game.gui;

import com.game.graphics.font.Font;
import com.game.math.Vector3f;

public class GUIButton extends GUIComponent implements IGUITextComponent
{
    private GUILabel label;

    public GUIButton(Vector3f position, float scale, int width, int height)
    {
        super(position, scale, width, height);
    }

    @Override
    public Font.TextRenderData getTextRenderData()
    {
        return label.getTextRenderData();
    }
}
