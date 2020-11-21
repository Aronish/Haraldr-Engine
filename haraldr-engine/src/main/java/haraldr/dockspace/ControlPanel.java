package haraldr.dockspace;

import haraldr.dockspace.uicomponents.LabeledComponent;
import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class ControlPanel extends DockablePanel
{
    private static final Vector4f DIVIDER_COLOR = new Vector4f(0.5f, 0.5f, 0.5f, 1f);
    private static final float LINE_GAP = 5f, SIDE_PADDING = 5f;

    private float divider;
    private float minDivider;

    private List<LabeledComponent> components = new ArrayList<>();

    public ControlPanel(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        super(position, size, color, name);
        divider = size.getX() * 0.5f;
    }

    @Override
    public void onEvent(Event event, Window window)
    {
        super.onEvent(event, window);
        /*
        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            var windowResizedEvent = (WindowResizedEvent) event;
            size.set(windowResizedEvent.width * widthRatio, windowResizedEvent.height);
            headerSize.setX(size.getX());
            orderComponents();
        }
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            var mousePressedEvent = (MousePressedEvent) event;
            resizing = resizable && mousePressedEvent.xPos > size.getX() - 10f && mousePressedEvent.xPos < size.getX() + 10f;
        }
        if (event.eventType == EventType.MOUSE_RELEASED)
        {
            if (resizing)
            {
                resizing = false;
                orderComponents();
            }
        }
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            if (resizing)
            {
                var mouseMovedEvent = (MouseMovedEvent) event;
                float width = (float) mouseMovedEvent.xPos;
                if (width < minDivider) width = minDivider;
                if (width > window.getWidth()) width = window.getWidth();
                size.setX(width);
                headerSize.setX(width);
            }
        }
        */
        components.forEach((component) -> component.onEvent(event));
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        for (LabeledComponent component : components)
        {
            component.setPosition(this.position, divider);
        }
        orderComponents();
    }

    @Override
    public void setSize(Vector2f size)
    {
        super.setSize(size);
        orderComponents();
    }

    @Override
    public void render(Batch2D batch)
    {
        renderSelf(batch);
        for (LabeledComponent component : components)
        {
            component.render(batch);
        }
    }

    private void renderSelf(Batch2D batch)
    {
        batch.drawQuad(position, headerSize, HEADER_COLOR);
        batch.drawQuad(Vector2f.add(position, new Vector2f(0f, headerSize.getY())), Vector2f.add(size, new Vector2f(0f, -headerSize.getY())), color);
        batch.drawQuad(Vector2f.add(position, new Vector2f(divider - 2f, headerSize.getY())), new Vector2f(2f, size.getY() - headerSize.getY()), DIVIDER_COLOR);
    }

    public void addChild(LabeledComponent component)
    {
        components.add(component);
        if (component.getLabelWidth() > minDivider)
        {
            divider = component.getLabelWidth() + SIDE_PADDING;
            minDivider = divider;
        }
        orderComponents();
    }

    private void orderComponents()
    {
        float nextY = headerSize.getY() + LINE_GAP;
        for (LabeledComponent component : components)
        {
            component.setPosition(Vector2f.add(position, new Vector2f(0f, nextY)), divider);
            component.setWidth(getComponentDivisionSize());
            nextY += component.getVerticalSize() + LINE_GAP;
        }
    }

    public float getSidePadding()
    {
        return SIDE_PADDING;
    }

    public float getComponentDivisionSize()
    {
        return size.getX() - divider - 2f * SIDE_PADDING;
    }
}
