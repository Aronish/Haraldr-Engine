package haraldr.ui;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;

public class UIHorizontalBreak extends UIComponent
{
    private int height;

    public UIHorizontalBreak(int height)
    {
        this.height = height;
    }

    @Override
    public void setWidth(float width)
    {
    }

    @Override
    public float getVerticalSize()
    {
        return height;
    }

    @Override
    public boolean onEvent(Event event, Window window)
    {
        return false;
    }

    @Override
    public void draw(Batch2D batch)
    {
    }

    @Override
    public void onDispose()
    {
    }
}
