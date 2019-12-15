package engine.gui;

import engine.gui.font.Font;

public interface IGUITextComponent
{
    Font.TextRenderData getTextRenderData();
    float[] getMatrixArray();
}