package haraldr.ui;

import haraldr.math.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class UIVerticalListGroup extends UIComponentGroup
{
    private List<UIPositionable> uiComponentList = new ArrayList<>();
    private float currentListHeight;

    public UIVerticalListGroup(UIContainer parent, int layerIndex)
    {
        super(parent, layerIndex);
    }

    public void addComponent(UIPositionable uiComponent)
    {
        uiComponentList.add(uiComponent);
        orderList();
    }

    private void orderList()
    {
        float currentListHeight = 0f;
        for (UIPositionable uiComponent : uiComponentList)
        {
            currentListHeight += uiComponent.getVerticalSize();
            uiComponent.setPosition(Vector2f.add(position, new Vector2f(0f, currentListHeight)));
        }
    }

    public void clear()
    {
        uiComponentList.clear();
    }

    @Override
    public void setPosition(Vector2f position)
    {
        this.position.set(position);
        orderList();
    }
}