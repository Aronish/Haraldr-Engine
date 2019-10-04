package com.game.gui;

import com.game.Window;
import com.game.gui.font.Font;
import com.game.math.Vector3f;

public class GUIButton extends GUIComponent implements IGUITextComponent
{
    private GUILabel label;

    public GUIButton(Vector3f position, Window window)
    {
        super(position);
    }

    @Override
    public Font.TextRenderData getTextRenderData()
    {
        return label.getTextRenderData();
    }

    @Override
    public void draw()
    {

    }
}
