package haraldr.graphics.ui;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
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
    private static final Font DEFAULT_FONT = new Font("default_fonts/Roboto-Regular.ttf", 18, 2);

    protected Vector2f position, size;
    private Vector2f headerSize;
    private float widthRatio, divider, dividerRatio, lineGap = 6f, sidePadding = 10f;
    private boolean resizing;

    protected TextBatch textBatch = new TextBatch(DEFAULT_FONT);
    protected TextLabel name;

    private List<LabeledComponent> components = new ArrayList<>();

    public Pane(Vector2f position, Vector2f initialSize, float widthRatio, float dividerRatio, String name)
    {
        this.position = position;
        size = initialSize;
        this.widthRatio = widthRatio;
        this.dividerRatio = dividerRatio;
        this.name = textBatch.createTextLabel(name, position, new Vector4f(1f));
        headerSize = new Vector2f(initialSize.getX(), DEFAULT_FONT.getSize() + 2f);
        divider = size.getX() * dividerRatio;
    }

    public void onWindowResized(float width, float height)
    {
        size.set(width * widthRatio, height);
        headerSize.setX(size.getX());
        divider = size.getX() * dividerRatio;
        orderComponents();
    }

    public void onEvent(Event event, Window window)
    {
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            var mousePressedEvent = (MousePressedEvent) event;
            resizing = mousePressedEvent.xPos > size.getX() - 10f &&
                    mousePressedEvent.xPos < size.getX() + 10f;
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
                size.setX(width);
                headerSize.setX(size.getX());
                divider = size.getX() * dividerRatio;
            }
        }
        components.forEach((component) -> component.onEvent(event));
    }

    public void onUpdate(float deltaTime)
    {
        components.forEach((component) -> component.onUpdate(deltaTime));
    }

    public void render()
    {
        renderSelf(position);
        for (LabeledComponent child : components)
        {
            child.render();
        }
    }

    private void renderSelf(Vector2f screenPosition)
    {
        Renderer2D.drawQuad(screenPosition, size, COLOR);
        Renderer2D.drawQuad(screenPosition, headerSize, HEADER_COLOR);
    }

    public void addChild(LabeledComponent component)
    {
        components.add(component);
        orderComponents();
    }

    private void orderComponents()
    {
        float nextY = headerSize.getY() + lineGap;
        for (LabeledComponent component : components)
        {
            component.setPosition(Vector2f.add(position, new Vector2f(0f, nextY)), divider);
            component.setWidth(getComponentDivisionSize());
            nextY += component.getVerticalSize() + lineGap;
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

    public float getComponentDivisionSize()
    {
        return size.getX() - divider - sidePadding;
    }

    public void renderText()
    {
        textBatch.render();
    }
}
