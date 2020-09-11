package haraldr.graphics.ui;

import haraldr.event.Event;
import haraldr.math.Vector2f;

public class HorizontalBreak extends LabeledComponent
{
    private int height = 20;

    public HorizontalBreak(String name, Pane parent)
    {
        super(name, parent);
    }

    public HorizontalBreak(String name, Pane parent, int height)
    {
        this(name, parent);
        this.height = height;
    }

    @Override
    protected void setComponentPosition(Vector2f position)
    {
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
    public void onEvent(Event event)
    {
    }

    @Override
    public void onUpdate(float deltaTime)
    {
    }

    @Override
    public void render()
    {
    }
}
