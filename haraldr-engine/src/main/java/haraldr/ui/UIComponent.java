package haraldr.ui;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;
import haraldr.math.Vector2f;

public abstract class UIComponent implements UIContainer
{
    protected Vector2f position = new Vector2f();
    private Batch2D mainBatch;
    protected TextBatch textBatch;
    protected boolean enabled = true;

    protected UIComponent(UIContainer parent)
    {
        mainBatch = parent.getMainBatch();
        textBatch = parent.getTextBatch();
    }

    public void setPosition(Vector2f position)
    {
        this.position.set(position);
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public abstract void onDispose();

    public abstract void setWidth(float width);

    public abstract boolean onEvent(Event event, Window window);

    public void draw()
    {
        draw(mainBatch);
    }

    public abstract void draw(Batch2D batch);

    public abstract float getVerticalSize();

    @Override
    public Batch2D getMainBatch()
    {
        return mainBatch;
    }

    @Override
    public TextBatch getTextBatch()
    {
        return textBatch;
    }
}