package dockspace;

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

import java.util.ArrayList;
import java.util.List;

public class DockablePanel
{
    private static final float HEADER_SIZE = 20f;
    private static final Vector4f HEADER_COLOR = new Vector4f(0.2f, 0.2f, 0.2f, 1f);

    private Vector2f position, relativePosition, size, headerSize;
    private Vector4f color;
    private boolean held, drawDockGizmo;

    private DockGizmo dockGizmo;

    private List<DockablePanel> children = new ArrayList<>();

    public DockablePanel(Vector2f position, Vector2f size, Vector4f color)
    {
        relativePosition = position;
        this.position = new Vector2f(relativePosition);
        headerSize = new Vector2f(size.getX(), HEADER_SIZE);
        this.size = size;
        this.color = color;
        dockGizmo = new DockGizmo(position, size);
    }

    public boolean onEvent(Event event, Window window)
    {
        if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
        {
            var mousePressedEvent = (MousePressedEvent) event;
            held = Physics2D.pointInsideAABB(new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos), position, headerSize);
        }
        if (Input.wasMouseReleased(event, MouseButton.MOUSE_BUTTON_1)) drawDockGizmo = held = false;
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

    public void render()
    {
        Renderer2D.drawQuad(position, size, color);
        Renderer2D.drawQuad(position, headerSize, HEADER_COLOR);
        children.forEach(DockablePanel::render);
        if (drawDockGizmo) dockGizmo.render();
    }

    public void addChild(DockablePanel panel)
    {
        panel.setPosition(position);
        children.add(panel);
    }

    public boolean select(MousePressedEvent event)
    {
        return Physics2D.pointInsideAABB(new Vector2f(event.xPos, event.yPos), position, size);
    }

    public void dockPanel(DockablePanel panel)
    {
        switch (dockGizmo.getDockPosition(panel.getPosition()))
        {
            //TOP and CENTER require panel tabs
            case BOTTOM ->
            {
                panel.setPosition(Vector2f.add(position, new Vector2f(0f, size.getY() / 2f)));
                panel.setSize(Vector2f.divide(size, new Vector2f(1f, 2f)));
            }
            case LEFT ->
            {
                panel.setPosition(position);
                panel.setSize(Vector2f.divide(size, new Vector2f(2f, 1f)));
            }
            case RIGHT ->
            {
                panel.setPosition(Vector2f.add(position, new Vector2f(size.getX() / 2f, 0f)));
                panel.setSize(Vector2f.divide(size, new Vector2f(2f, 1f)));
            }
        }
    }

    public void setDrawDockGizmo(boolean drawDockGizmo)
    {
        this.drawDockGizmo = drawDockGizmo;
    }

    public void setPosition(Vector2f parentPosition)
    {
        position.set(parentPosition);
        dockGizmo.setPosition(parentPosition);
        for (DockablePanel child : children)
        {
            child.setPosition(Vector2f.add(relativePosition, parentPosition));
        }
    }

    public void setSize(Vector2f size)
    {
        this.size = size;
        headerSize.setX(size.getX());
        dockGizmo.setSize(size);
    }

    public Vector2f getPosition()
    {
        return position;
    }

    public Vector2f getSize()
    {
        return size;
    }

    @Override
    public String toString()
    {
        return color.toString();
    }
}
