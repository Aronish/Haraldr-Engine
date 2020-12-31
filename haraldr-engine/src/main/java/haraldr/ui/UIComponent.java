package haraldr.ui;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;
import haraldr.math.Vector2f;

public abstract class UIComponent
{
    protected Vector2f position = new Vector2f();

    public void setPosition(Vector2f position)
    {
        this.position.set(position);
    }

    public void addPosition(Vector2f position)
    {
        this.position.add(position);
    }

    public abstract void setWidth(float width);

    public abstract float getVerticalSize();

    /**
     * @return true if an event requires a redraw.
     */
    public abstract boolean onEvent(Event event, Window window);

    public abstract void draw(Batch2D batch);

    public abstract void onDispose();
}