package haraldr.ui;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.graphics.TextBatchContainer;
import haraldr.main.Window;
import haraldr.math.Vector2f;

public abstract class UIComponent implements TextBatchContainer
{
    protected Vector2f position = new Vector2f();
    protected TextBatch textBatch;

    protected UIComponent(TextBatchContainer parent)
    {
        textBatch = parent.getTextBatch();
    }

    public void setPosition(Vector2f position)
    {
        this.position.set(position);
    }

    public void addPosition(Vector2f position) // Probably useless unless performance really sucks
    {
        this.position.add(position);
    }

    public abstract void onDispose();

    public abstract void setWidth(float width);

    public abstract boolean onEvent(Event event, Window window);

    public abstract void draw(Batch2D batch);

    public abstract float getVerticalSize();

    @Override
    public TextBatch getTextBatch()
    {
        return textBatch;
    }
}