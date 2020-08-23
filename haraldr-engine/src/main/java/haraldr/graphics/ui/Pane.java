package haraldr.graphics.ui;

import haraldr.event.Event;
import haraldr.graphics.Renderer2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Pane extends UIComponent
{
    private static final Vector4f HEADER_COLOR = new Vector4f(0.2f, 0.8f, 0.4f, 1f);

    private Vector2f headerSize;
    private Vector4f color;

    private float nextY, padding = 5f;
    private List<UIComponent> components = new ArrayList<>();

    public Pane(Vector2f position, Vector2f size, Vector4f color, String name)
    {
        super(position, size, name);
        this.color = color;
        headerSize = new Vector2f(size.getX(), DEFAULT_FONT.getSize() + 2f);
        nextY = headerSize.getY();
    }

    @Override
    protected void setupLabel(String name)
    {
    }

    @Override
    public void setSize(int width, int height)
    {
        super.setSize(width, height);
        headerSize.setX(width);
    }

    @Override
    public void onEvent(Event event)
    {
        components.forEach((component) -> component.onEvent(event));
    }

    @Override
    public void render(Vector2f parentPosition)
    {
        Vector2f screenPosition = Vector2f.add(parentPosition, position);
        renderSelf(screenPosition);
        for (UIComponent child : components)
        {
            child.render(screenPosition);
        }
    }

    private void renderSelf(Vector2f screenPosition)
    {
        Renderer2D.drawQuad(screenPosition, size, color);
        Renderer2D.drawQuad(screenPosition, headerSize, HEADER_COLOR);
    }

    public void addChild(UIComponent component)
    {
        components.add(component);
        orderComponents();
    }

    private void orderComponents()
    {
        nextY = headerSize.getY() + padding;
        for (int i = 0; i < components.size(); ++i)
        {
            UIComponent component = components.get(i);
            component.setPosition(Vector2f.add(position, new Vector2f(padding, nextY)));
            nextY += component.size.getY() + padding;
        }
    }

    public TextBatch getTextBatch()
    {
        return textBatch;
    }
}
