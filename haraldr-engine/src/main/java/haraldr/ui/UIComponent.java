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
        UILayer layer = parent.getLayer(layerIndex);
        layer.addComponent(this);
        batch = layer.getBatch();
        textBatch = layer.getTextBatch();
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
    public UILayer getLayer(int index)
    {
        return parent.getLayer(index);
    }

    @Override
    public List<UILayer> getLayers()
    {
        return parent.getLayers();
    }
}