package haraldr.ui;

import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;

public abstract class UIComponent implements UIComponentBehavior, UIContainer
{
    protected Vector2f position = new Vector2f();
    private Batch2D mainBatch, overlayBatch; // List of batches if needed with getter with index.
    protected TextBatch textBatch;
    protected boolean enabled = true;

    protected UIComponent(UIContainer parent)
    {
        mainBatch = parent.getMainBatch();
        textBatch = parent.getTextBatch();
        overlayBatch = parent.getOverlayBatch();
    }

    public void setPosition(Vector2f position)
    {
        this.position.set(position);
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void draw()
    {
        draw(mainBatch);
    }

    @Override
    public Batch2D getMainBatch()
    {
        return mainBatch;
    }

    @Override
    public Batch2D getOverlayBatch()
    {
        return overlayBatch;
    }

    @Override
    public TextBatch getTextBatch()
    {
        return textBatch;
    }
}