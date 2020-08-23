package haraldr.graphics.ui;

import haraldr.event.Event;
import haraldr.graphics.Renderer2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Pane extends UIComponent
{
    private static final Font DEFAULT_FONT = new Font("default_fonts/consola.ttf", 20);
    private static final Vector4f HEADER_COLOR = new Vector4f(0.2f, 0.8f, 0.4f, 1f);

    private Vector2f headerSize;
    private Vector4f color;
    private TextBatch textBatch = new TextBatch(DEFAULT_FONT);

    private GridLayout layout;
    private List<UIComponent> components = new ArrayList<>();

    public Pane(Vector2f position, Vector2f size, Vector4f color, String name, GridLayout layout)
    {
        super(position, size);
        this.color = color;
        this.layout = layout;
        headerSize = new Vector2f(size.getX(), DEFAULT_FONT.getSize());
        textBatch.createTextLabel(name, position, new Vector4f(1f));
    }

    @Override
    public void setSize(int width, int height)
    {
        super.setSize(width, height);
        headerSize.setX(width);
        layout.setSize(width, height);
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

    public void renderText()
    {
        textBatch.render();
    }

    private void renderSelf(Vector2f screenPosition)
    {
        Renderer2D.drawQuad(screenPosition, size, color);
        Renderer2D.drawQuad(screenPosition, headerSize, HEADER_COLOR);
    }

    public void addChild(UIComponent newComponent)
    {
        if (components.size() < layout.getMaxSlots())
        {
            components.add(newComponent);
            refresh(position);
        }
    }

    public void refresh(Vector2f parentPosition)
    {
        layout.orderComponents(components, parentPosition, 0);
    }

    public Vector2f getLayoutSize()
    {
        return layout.getSize();
    }

    public GridLayout getLayout()
    {
        return layout;
    }
}
