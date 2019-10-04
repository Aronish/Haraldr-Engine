package com.game.gui;

import com.game.gui.font.Font;

public interface IGUITextComponent
{
    Font.TextRenderData getTextRenderData();
    float[] getMatrixArray();
}