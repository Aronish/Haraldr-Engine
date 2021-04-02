package haraldr.ui.components;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.input.Input;
import haraldr.input.MouseButton;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;
import haraldr.ui.TextBatch;
import haraldr.ui.TextLabel;

public class ListItem
{
    private TextLabel tag;
    private Vector2f position, size;
    private boolean hovered;
    private ListItemCallback listItemCallback;

    public ListItem(String name, Vector2f position, TextBatch textBatch, boolean enabled, ListItemCallback listItemCallback)
    {
        this.position = new Vector2f(position);
        this.tag = textBatch.createTextLabel(name, this.position, new Vector4f(1f), enabled);
        size = new Vector2f(0f, textBatch.getFont().getSize());
        this.listItemCallback = listItemCallback;
    }

    record ListItemEventResult(boolean requiresRedraw, boolean consumed, ListItem pressedItem) {}

    public ListItemEventResult onEvent(Event event)
    {
        boolean requiresRedraw = false, pressed = false;
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            var mouseMovedEvent = (MouseMovedEvent) event;
            boolean previousHoveredState = hovered;
            hovered = Physics2D.pointInsideAABB(new Vector2f(mouseMovedEvent.xPos, mouseMovedEvent.yPos), position, size);
            if (previousHoveredState != hovered) requiresRedraw = true;
        }
        if (event.eventType == EventType.MOUSE_PRESSED && Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
        {
            var mousePressedEvent = (MousePressedEvent) event;
            pressed = Physics2D.pointInsideAABB(new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos), position, size);
            if (pressed)
            {
                requiresRedraw = true;
            }
        }
        return new ListItemEventResult(requiresRedraw, hovered || pressed , pressed ? this : null);
    }

    public void setPosition(Vector2f position)
    {
        this.position.set(position);
        tag.setPosition(position);
    }

    public void setWidth(float width)
    {
        this.size.setX(width);
    }

    public TextLabel getTag()
    {
        return tag;
    }

    public Vector2f getPosition()
    {
        return position;
    }

    public Vector2f getSize()
    {
        return size;
    }

    public boolean isHovered()
    {
        return hovered;
    }

    public ListItemCallback getListItemCallback()
    {
        return listItemCallback;
    }

    public interface ListItemCallback
    {
        void onPress();
    }
}