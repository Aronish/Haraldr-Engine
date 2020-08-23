package haraldr.graphics.ui;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.event.MouseReleasedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class Button extends UIComponent
{
    private static final Vector4f ON_COLOR = new Vector4f(0.2f, 0.8f, 0.3f, 1f);
    private static final Vector4f OFF_COLOR = new Vector4f(0.8f, 0.2f, 0.3f, 1f);

    private boolean active;

    public Button()
    {
        super(new Vector2f(), new Vector2f(), "");
    }

    public Button(boolean initialState)
    {
        super(new Vector2f(), new Vector2f(), "");
        active = initialState;
    }

    public Button(Vector2f position, Vector2f size)
    {
        super(position, size, "");
    }

    public void onClick(int x, int y)
    {
        if (x >= position.getX() && x <= position.getX() + size.getX() && y >= position.getY() && y <= position.getY() + size.getY())
        {
            active = !active;
        }
    }

    @Override
    protected void setupLabel(String name)
    {

    }

    @Override
    public void onEvent(Event event)
    {
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            var mousePressedEvent = (MousePressedEvent) event;
            onClick(mousePressedEvent.xPos, mousePressedEvent.yPos);
        }
    }

    @Override
    public void render(Vector2f parentPosition)
    {
        Renderer2D.drawQuad(Vector2f.add(parentPosition, position), size, active ? ON_COLOR : OFF_COLOR);
    }
}
