package gui;

import gui.font.Font;

public interface IGUITextComponent
{
    Font.TextRenderData getTextRenderData();
    float[] getMatrixArray();
}