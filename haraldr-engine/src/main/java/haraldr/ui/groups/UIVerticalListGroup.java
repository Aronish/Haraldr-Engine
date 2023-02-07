package haraldr.ui.groups;

import haraldr.math.Vector2f;
import haraldr.ui.components.UIPositionable;

import java.util.ArrayList;
import java.util.List;

public class UIVerticalListGroup extends UIComponentGroup<VerticalListInsertData>
{
    private List<UIPositionable> uiComponentList = new ArrayList<>();

    public void addComponent(VerticalListInsertData verticalListInsertData)
    {
        uiComponentList.add(verticalListInsertData.component());
        orderList();
    }

    private void orderList()
    {
        float currentListHeight = 0f;
        for (UIPositionable uiComponent : uiComponentList)
        {
            uiComponent.setPosition(Vector2f.addY(position, currentListHeight));
            uiComponent.setSize(new Vector2f(size.getX(), uiComponent.getSize().getY()));
            currentListHeight += uiComponent.getVerticalSize();
        }
    }

    public void clear()
    {
        uiComponentList.clear();
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
            uiComponent.setSize(new Vector2f(size.getX(), uiComponent.getVerticalSize()));
        }
        orderList();
    }
}