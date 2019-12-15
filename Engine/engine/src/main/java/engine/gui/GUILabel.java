package engine.gui;

import engine.gui.font.Font;
import engine.gui.font.PackedFont;

//TODO Probably shouldn't be able to define size.
public class GUILabel //extends GUIComponent implements IGUITextComponent
{
    private PackedFont font;
    private Font.TextRenderData textRenderData;
    public String text;
/*BROKEN!!!
    public GUILabel(Vector3f position, PackedFont font, String text, int textSizePx, int width, int height)
    {
        super(position, font.getScaleFactor(textSizePx) * (float) width / height, width, height);
        this.font = font;
        setText(text);
    }*/

    public void setText(String text)
    {
        this.text = text;
        textRenderData = font.createTextRenderData(text);
    }

    //@Override
    public Font.TextRenderData getTextRenderData()
    {
        return textRenderData;
    }
}
