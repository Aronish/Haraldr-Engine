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
    private ListItemPressAction listItemPressAction;

    public ListItem(String name, Vector2f position, TextBatch textBatch, ListItemPressAction listItemPressAction)
    {
        this.position = new Vector2f(position);
        this.tag = textBatch.createTextLabel(name, this.position, new Vector4f(1f), false);
        size = new Vector2f(0f, textBatch.getFont().getSize());
        this.listItemPressAction = listItemPressAction;
    }

    public boolean onEvent(Event event)
    {
        boolean requireRedraw = false;
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            var mouseMovedEvent = (MouseMovedEvent) event;
            boolean previousHoveredState = hovered;
            hovered = Physics2D.pointInsideAABB(new Vector2f(mouseMovedEvent.xPos, mouseMovedEvent.yPos), position, size);
            requireRedraw = previousHoveredState != hovered;
        }
        if (event.eventType == EventType.MOUSE_PRESSED && Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
        {
            var mousePressedEvent = (MousePressedEvent) event;
            boolean lastPressed = pressed;
            pressed = Physics2D.pointInsideAABB(new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos), position, size);
            if (pressed) listItemPressAction.run(tag.getText());
            requireRedraw = lastPressed != pressed;
        }
        if (Input.wasMouseReleased(event, MouseButton.MOUSE_BUTTON_1))
        {
            requireRedraw = pressed;
            pressed = false;
        }
        return requireRedraw;
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

    @FunctionalInterface
    public interface ListItemPressAction
    {
        void run(String name);
    }
}