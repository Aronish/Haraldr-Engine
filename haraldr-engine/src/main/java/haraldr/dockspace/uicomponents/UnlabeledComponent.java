package haraldr.dockspace.uicomponents;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;

public abstract class UnlabeledComponent
{
    protected Vector2f position = new Vector2f();

    public void setPosition(Vector2f position)
    {
        this.position.set(position);
    }

    public abstract void setWidth(float width);

    public abstract float getVerticalSize();

    /**
     * @return true if an event requires a ControlPanel to redraw.
     */
    public abstract boolean onEvent(Event event);

    public abstract void render(Batch2D batch);
}