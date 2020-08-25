package haraldr.graphics.ui;

import haraldr.event.Event;
import haraldr.graphics.Renderer2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Pane
{
    private static final Vector4f COLOR = new Vector4f(0.3f, 0.3f, 0.3f, 1f);
    private static final Vector4f HEADER_COLOR = new Vector4f(0.2f, 0.2f, 0.2f, 1f);
    private static final Font DEFAULT_FONT = new Font("default_fonts/consola.ttf", 40);

    protected Vector2f position, size;
    private Vector2f headerSize;
    private float divider;

    protected TextBatch textBatch = new TextBatch(DEFAULT_FONT);
    protected TextLabel name;

    private List<LabeledComponent> components = new ArrayList<>();

    public Pane(Vector2f position, Vector2f size, String name)
    {
        this.position = position;
        this.size = size;
        this.name = textBatch.createTextLabel(name, position, new Vector4f(1f));
        headerSize = new Vector2f(size.getX(), DEFAULT_FONT.getSize() + 2f);
        divider = size.getX() / 2f - 30f;
    }

    public void setSize(int width, int height)
    {
        headerSize.setX(width);
    }

    public void onEvent(Event event)
    {
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
        float nextY = headerSize.getY();
        for (LabeledComponent component : components)
        {
            component.setPosition(Vector2f.add(position, new Vector2f(0f, nextY)));
            nextY += component.getVerticalSize();
        }
    }

    public float getDivider()
    {
        return divider;
    }

    public void renderText()
    {
        textBatch.render();
    }
}
