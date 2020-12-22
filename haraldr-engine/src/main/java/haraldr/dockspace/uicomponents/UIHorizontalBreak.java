package haraldr.dockspace.uicomponents;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;

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
    public boolean onEvent(Event event)
    {
        return false;
    }

    @Override
    public void render(Batch2D batch)
    {
    }
}
