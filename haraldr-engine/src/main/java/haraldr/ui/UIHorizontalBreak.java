package haraldr.ui;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.graphics.TextBatchContainer;
import haraldr.main.Window;

public class UIHorizontalBreak extends UIComponent // Quite useless as a whole class
{
    private int height;

    public UIHorizontalBreak(int height)
    {
        this(null, height);
    }

    public UIHorizontalBreak(TextBatchContainer parent, int height)
    {
        super(parent);
        this.height = height;
    }

    @Override
    public void setWidth(float width)
    {
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

    @Override
    public float getVerticalSize()
    {
        return height;
    }
}
