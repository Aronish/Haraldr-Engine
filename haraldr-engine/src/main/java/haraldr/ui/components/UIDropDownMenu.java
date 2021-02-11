package haraldr.ui.components;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.input.Input;
import haraldr.input.MouseButton;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

//TODO: Open menu upwards if no space
public class UIDropDownMenu extends UIComponent
{
    private boolean menuCloseRequested;
    private UIInfoLabel selected;
    private UIVerticalList verticalList;

    public UIDropDownMenu(UIContainer parent, int layerIndex)
    {
        super(parent, layerIndex);
        size = new Vector2f(20f);
        verticalList = new UIVerticalList(this, layerIndex + 1, new Vector4f(0.4f, 0.4f, 0.4f, 1f));
        verticalList.setEnabled(false);
        selected = new UIInfoLabel(this, layerIndex, "");
    }

    public void addMenuItem(String name, ListItem.ListItemCallback listItemCallback)
    {
        verticalList.addItem(name, () ->
        {
            selected.setValue(name);
            listItemCallback.onPress();
            menuCloseRequested = true;
        });
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        verticalList.setPosition(Vector2f.addY(position, size.getY()));
        selected.setPosition(position);
    }

    @Override
    public void setSize(Vector2f size)
    {
        super.setSize(size);
        verticalList.setSize(size);
    }

    @Override
    public float getVerticalSize()
    {
        return size.getY() + verticalList.getVerticalSize();
    }

    @Override
    public UIEventResult onEvent(Event event, Window window)
    {
        boolean requiresRedraw = menuCloseRequested;
        if (menuCloseRequested)
        {
            verticalList.setEnabled(false);
            menuCloseRequested = false;
        } else if (event.eventType == EventType.MOUSE_PRESSED && Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
        {
            var mousePressedEvent = (MousePressedEvent) event;
            Vector2f mousePoint = new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos);

            if (Physics2D.pointInsideAABB(mousePoint, position, size))
            {
                verticalList.setEnabled(!verticalList.isEnabled());
            }
            requiresRedraw = true;
        }
        return new UIEventResult(requiresRedraw, false);
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        selected.setEnabled(enabled);
        if (!enabled) verticalList.setEnabled(false);
    }
}