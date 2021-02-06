package haraldr.ui;

import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class UIVerticalListGroup extends UIComponentGroup
{
    private List<UIComponent> uiComponentList = new ArrayList<>();
    private float currentListHeight;

    protected UIVerticalListGroup(UIContainer parent, int layerIndex)
    {
        super(parent, layerIndex);
    }

    public void addComponent(UIComponent uiComponent)
    {
        uiComponentList.add(uiComponent);
        orderList();
    }

    private void orderList()
    {
        float currentListHeight = 0f;
        for (UIComponent uiComponent : uiComponentList)
        {
            currentListHeight += uiComponent.getVerticalSize();
            uiComponent.setPosition(Vector2f.add(position, new Vector2f(0f, currentListHeight)));
        }
    }

    @Override
    public void setPosition(Vector2f position)
    {
        this.position.set(position);
        orderList();
    }

    @Override
    public void draw(Batch2D batch)
    {
        for (UIComponent uiComponent : uiComponentList)
        {
            uiComponent.draw(batch);
        }
    }
}