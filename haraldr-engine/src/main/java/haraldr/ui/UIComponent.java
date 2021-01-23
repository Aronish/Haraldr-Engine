package haraldr.ui;

import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;

import java.util.List;

public abstract class UIComponent implements UIComponentBehavior, UIContainer
{
    protected Vector2f position = new Vector2f();
    protected boolean enabled = true;
    protected UIContainer parent;
    protected Batch2D batch;
    protected TextBatch textBatch;

    protected UIComponent(UIContainer parent, int layerIndex)
    {
        this.parent = parent;
        UIEventLayer layer = parent.getLayer(layerIndex);
        layer.addComponent(this);
        batch = layer.getBatch();
        textBatch = layer.getTextBatch();
    }

    public void addPosition(Vector2f difference)
    {
        position.add(difference);
    }

    public void setPosition(Vector2f position)
    {
        this.position.set(position);
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public UIEventLayer getLayer(int index)
    {
        return parent.getLayer(index);
    }

    @Override
    public List<UIEventLayer> getLayers()
    {
        return parent.getLayers();
    }
}