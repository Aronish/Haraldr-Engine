package haraldr.ui;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.event.ParentCollapsedEvent;
import haraldr.graphics.Batch2D;
import haraldr.input.Input;
import haraldr.input.MouseButton;
import haraldr.main.Layer;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.physics.Physics2D;

public class UIDropDownMenu extends UIComponent
{
    private Vector2f size;
    private boolean menuOpened;
    private UIVerticalList verticalList;

    public UIDropDownMenu(Layer contextMenuLayer)
    {
        size = new Vector2f(20f);
        verticalList = new UIVerticalList(contextMenuLayer);
    }

    public void addMenuItem(String name, ListItem.ListItemPressAction listItemPressAction)
    {
        verticalList.addItem(name, listItemPressAction);
    }

    public void setMenuOpened(boolean menuOpened)
    {
        this.menuOpened = menuOpened;
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        verticalList.setPosition(position);
    }

    @Override
    public void setWidth(float width)
    {
        size.setX(width);
        verticalList.setWidth(width);
    }

    @Override
    public float getVerticalSize()
    {
        return size.getY() + verticalList.getVerticalSize();
    }

    @Override
    public boolean onEvent(Event event, Window window)
    {
        boolean requireRedraw = false;
        if (menuOpened)
        {
            requireRedraw = verticalList.onEvent(event, window);
        }
        if (event.eventType == EventType.MOUSE_PRESSED && Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
        {
            var mousePressedEvent = (MousePressedEvent) event;
            Vector2f mousePoint = new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos);

            if (Physics2D.pointInsideAABB(mousePoint, position, size)) menuOpened = !menuOpened;
            else menuOpened = false;

            verticalList.setVisible(menuOpened);
            requireRedraw = true;
        }
        if (event.eventType == EventType.PARENT_COLLAPSED)
        {
            var parentCollapsedEvent = (ParentCollapsedEvent) event;
            verticalList.setVisible(!parentCollapsedEvent.collapsed);
        }
        return requireRedraw;
    }

    @Override
    public void draw(Batch2D batch)
    {
        verticalList.draw(batch);
    }

    @Override
    public void onDispose()
    {
    }

    public boolean isMenuOpened()
    {
        return menuOpened;
    }
}
