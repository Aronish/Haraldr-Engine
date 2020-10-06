package haraldr.dockspace;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.input.Input;
import haraldr.input.MouseButton;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

public class DockablePanel
{
    private static final float HEADER_SIZE = 20f;
    private static final Vector4f HEADER_COLOR = new Vector4f(0.2f, 0.2f, 0.2f, 1f);

    private Vector2f position, size, headerSize;
    private Vector4f color;
    private boolean held;

    private PanelDimensionChangeAction panelDimensionChangeAction = (position, size) -> {};

    public DockablePanel(Vector2f position, Vector2f size, Vector4f color)
    {
        this.position = new Vector2f(position);
        headerSize = new Vector2f(size.getX(), HEADER_SIZE);
        this.size = size;
        this.color = color;
    }

    public void onEvent(Event event, Window window)
    {
        if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
        {
            var mousePressedEvent = (MousePressedEvent) event;
            held = Physics2D.pointInsideAABB(new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos), position, headerSize);
        }
        if (Input.wasMouseReleased(event, MouseButton.MOUSE_BUTTON_1)) held = false;
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            if (held)
            {
                var mouseMovedEvent = (MouseMovedEvent) event;
                setPosition(new Vector2f(mouseMovedEvent.xPos, mouseMovedEvent.yPos));
            }
        }
    }

    public void render()
    {
        Renderer2D.drawQuad(position, size, color);
        Renderer2D.drawQuad(position, headerSize, HEADER_COLOR);
    }

    public void setPanelResizeAction(PanelDimensionChangeAction panelDimensionChangeAction)
    {
        this.panelDimensionChangeAction = panelDimensionChangeAction;
    }

    public void setPosition(Vector2f parentPosition)
    {
        position.set(parentPosition);
        panelDimensionChangeAction.run(
                Vector2f.add(position, new Vector2f(0f, headerSize.getY())),
                Vector2f.add(size, new Vector2f(0f, -headerSize.getY()))
        );
    }

    public void setSize(Vector2f size)
    {
        this.size.set(size);
        headerSize.setX(size.getX());
        panelDimensionChangeAction.run(
                Vector2f.add(position, new Vector2f(0f, headerSize.getY())),
                Vector2f.add(size, new Vector2f(0f, -headerSize.getY()))
        );
    }

    public void addSize(Vector2f size)
    {
        this.size.add(size);
        this.headerSize.addX(size.getX());
        panelDimensionChangeAction.run(
                Vector2f.add(position, new Vector2f(0f, headerSize.getY())),
                Vector2f.add(size, new Vector2f(0f, -headerSize.getY()))
        );
    }

    public Vector2f getPosition()
    {
        return position;
    }

    public Vector2f getSize()
    {
        return size;
    }

    public float getHeaderHeight()
    {
        return headerSize.getY();
    }

    public boolean isHeld()
    {
        return held;
    }

    public interface PanelDimensionChangeAction
    {
        void run(Vector2f position, Vector2f size);
    }
}
