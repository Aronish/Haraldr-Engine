package haraldr.ui;

import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;

public abstract class UIComponentGroup extends UIComponent
{
    protected UIComponentGroup(UIContainer parent, int layerIndex)
    {
        super(parent, layerIndex);
    }

    public abstract void setPosition(Vector2f position);

    @Override
    public abstract void draw(Batch2D batch);
}