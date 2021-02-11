package haraldr.ui.components;

import haraldr.math.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class UIVerticalListGroup extends UIComponentGroup
{
    private List<UIPositionable> uiComponentList = new ArrayList<>();

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
            uiComponent.setPosition(Vector2f.addY(position, currentListHeight));
            uiComponent.setSize(size);
            currentListHeight += uiComponent.getVerticalSize();
        }
    }

    public void clear()
    {
        uiComponentList.clear();
    }

    @Override
    public void update()
    {
        orderList();
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        orderList();
    }

    @Override
    public void setSize(Vector2f size)
    {
        super.setSize(size);
        for (UIPositionable uiComponent : uiComponentList)
        {
            uiComponent.setSize(size);
        }
        orderList();
    }
}