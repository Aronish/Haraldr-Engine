package dockspace;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseButtonEvent;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.input.Input;
import haraldr.input.MouseButton;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class DockablePanel
{
    private Vector2f position, relativePosition, size;
    private Vector4f color;
    private boolean held;

    private List<DockablePanel> children = new ArrayList<>();

    public DockablePanel(Vector2f position, Vector2f size, Vector4f color)
    {
        relativePosition = position;
        this.position = new Vector2f(relativePosition);
        this.size = size;
        this.color = color;
    }

    public void addChild(DockablePanel panel)
    {
        panel.setPosition(position);
        children.add(panel);
    }

    public boolean select(MousePressedEvent event)
    {
        return held =
        event.xPos >= position.getX() && event.xPos <= position.getX() + size.getX() &&
        event.yPos >= position.getY() && event.yPos <= position.getY() + size.getY();
    }

    public boolean onEvent(Event event, Window window)
    {
        if (Input.wasMouseReleased(event, MouseButton.MOUSE_BUTTON_1)) held = false;
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            if (held)
            {
                var mouseMovedEvent = (MouseMovedEvent) event;
                setPosition(new Vector2f(mouseMovedEvent.xPos, mouseMovedEvent.yPos));
            }
        }
        return held;
    }

    public void setPosition(Vector2f parentPosition)
    {
        position.set(parentPosition);
        children.forEach((panel) -> panel.setPosition(Vector2f.add(relativePosition, parentPosition)));
    }

    public void render()
    {
        Renderer2D.drawQuad(position, size, color);
        children.forEach(DockablePanel::render);
    }

    public Vector2f getPosition()
    {
        return position;
    }
}
