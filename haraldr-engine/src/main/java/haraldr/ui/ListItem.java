package haraldr.ui;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.input.Input;
import haraldr.input.MouseButton;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

public class ListItem
{
    private TextLabel tag;
    private Vector2f position, size;
    private boolean hovered, pressed;
    private ListItemCallback listItemCallback;

    public ListItem(String name, Vector2f position, TextBatch textBatch, boolean enabled, ListItemCallback listItemCallback)
    {
        this.position = new Vector2f(position);
        this.tag = textBatch.createTextLabel(name, this.position, new Vector4f(1f), enabled);
        size = new Vector2f(0f, textBatch.getFont().getSize());
        this.listItemCallback = listItemCallback;
    }

    record ListItemEventResult(boolean requiresRedraw, ListItem pressedItem) {}

    public ListItemEventResult onEvent(Event event)
    {
        boolean requiresRedraw = false;
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            var mouseMovedEvent = (MouseMovedEvent) event;
            boolean previousHoveredState = hovered;
            hovered = Physics2D.pointInsideAABB(new Vector2f(mouseMovedEvent.xPos, mouseMovedEvent.yPos), position, size);
            if (hovered) event.setHandled(true);
            if (previousHoveredState != hovered) requiresRedraw = true;
        }
        if (event.eventType == EventType.MOUSE_PRESSED && Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
        {
            var mousePressedEvent = (MousePressedEvent) event;
            boolean lastPressed = pressed;
            pressed = Physics2D.pointInsideAABB(new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos), position, size);
            if (pressed) event.setHandled(true);
            if (lastPressed != pressed) requiresRedraw = true;
        }
        if (Input.wasMouseReleased(event, MouseButton.MOUSE_BUTTON_1))
        {
            if (pressed) requiresRedraw = true;
            pressed = false;
        }
        return new ListItemEventResult(requiresRedraw, pressed ? this : null);
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

    public boolean isPressed()
    {
        return pressed;
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