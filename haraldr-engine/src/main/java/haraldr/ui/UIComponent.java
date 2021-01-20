package haraldr.ui;

import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;

public abstract class UIComponent implements UIComponentBehavior, UIContainer
{
    protected Vector2f position = new Vector2f();
    protected Batch2D batch;
    protected TextBatch textBatch;
    protected boolean enabled = true;

    protected UIComponent(UIContainer parent) //NEED ACCESS TO OTHER LAYERS
    {
        batch = parent.getBatch();
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

    public void draw()
    {
        draw(batch);
    }
}