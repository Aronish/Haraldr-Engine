package haraldr.graphics.ui;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.event.WindowResizedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Pane
{
    private static final Vector4f COLOR = new Vector4f(0.3f, 0.3f, 0.3f, 1f);
    private static final Vector4f HEADER_COLOR = new Vector4f(0.2f, 0.2f, 0.2f, 1f);
    private static final Vector4f DIVIDER_COLOR = new Vector4f(0.5f, 0.5f, 0.5f, 1f);
    private static final Font DEFAULT_FONT = new Font("default_fonts/Roboto-Regular.ttf", 18, 2);
    private static final float LINE_GAP = 5f, SIDE_PADDING = 5f;

    protected Vector2f position;
    private Vector2f size, headerSize;
    private float widthRatio, divider, dividerRatio, minDivider;
    private boolean resizable, resizing;

    protected TextBatch textBatch = new TextBatch(DEFAULT_FONT);
    protected TextLabel name;

    private List<LabeledComponent> components = new ArrayList<>();

    public Pane(Vector2f position, float windowWidth, float windowHeight, float widthRatio, float dividerRatio, boolean resizable, String name)
    {
        this.position = position;
        size = new Vector2f(windowWidth * widthRatio, windowHeight);
        this.widthRatio = widthRatio;
        this.dividerRatio = dividerRatio;
        this.name = textBatch.createTextLabel(name, position, new Vector4f(1f));
        headerSize = new Vector2f(size.getX(), DEFAULT_FONT.getSize() + 2f);
        divider = size.getX() * dividerRatio;
        this.resizable = resizable;
    }

    public boolean onEvent(Event event, Window window)
    {
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
                if (width < 0) width = 0;
                if (width > window.getWidth()) width = window.getWidth();
                if (width * dividerRatio > minDivider)
                {
                    size.setX(width);
                    headerSize.setX(size.getX());
                    divider = width * dividerRatio;
                } else
                {
                    divider = minDivider;
                }
            }
        }
        components.forEach((component) -> component.onEvent(event));
        return resizing;
    }

    public void onUpdate(float deltaTime)
    {
        components.forEach((component) -> component.onUpdate(deltaTime));
    }

    public void render()
    {
        renderSelf();
        for (LabeledComponent child : components)
        {
            child.render();
        }
    }

    private void renderSelf()
    {
        Renderer2D.drawQuad(position, size, COLOR);
        Renderer2D.drawQuad(Vector2f.add(position, new Vector2f(divider - 2f, 0f)), new Vector2f(2f, size.getY()), DIVIDER_COLOR);
        Renderer2D.drawQuad(position, headerSize, HEADER_COLOR);
    }

    public void renderText()
    {
        textBatch.render();
    }

    public void addChild(LabeledComponent component)
    {
        components.add(component);
        if (component.label.getPixelWidth() > minDivider)
        {
            divider = component.label.getPixelWidth() + SIDE_PADDING;
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

    public Vector2f getPosition()
    {
        return position;
    }

    public Vector2f getSize()
    {
        return size;
    }

    public float getSidePadding()
    {
        return SIDE_PADDING;
    }

    public TextBatch getTextBatch()
    {
        return textBatch;
    }

    public float getComponentDivisionSize()
    {
        return size.getX() - divider - 2f * SIDE_PADDING;
    }
}
