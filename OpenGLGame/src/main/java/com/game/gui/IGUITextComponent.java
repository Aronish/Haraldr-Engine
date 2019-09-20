package com.game.gui;

import com.game.graphics.font.Font;

public interface IGUITextComponent
{
    Font.TextRenderData getTextRenderData();
    float[] getMatrixArray();
}