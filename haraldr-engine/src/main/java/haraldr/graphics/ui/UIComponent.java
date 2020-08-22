package haraldr.graphics.ui;

import haraldr.event.Event;
import haraldr.math.Vector2f;

public abstract class UIComponent
{
    protected Vector2f position, size;

    public UIComponent(Vector2f position, Vector2f size)
    {
        this.position = position;
        this.size = size;
    }

    public void setPosition(int x, int y)
    {
        position.set(x, y);
    }

    public void setSize(int width, int height)
    {
        size.set(width, height);
    }

    public Vector2f getPosition()
    {
        return position;
    }

    public Vector2f getSize()
    {
        return size;
    }

    public abstract void onEvent(Event event);

    public abstract void render(Vector2f parentPosition);
}
