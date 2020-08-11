package haraldr.graphics.ui;

import haraldr.graphics.Renderer2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class Pane extends UIComponent
{
    private static final float HEADER_THICKNESS = 30f, WIDHT_PADDING = 10f, HEIGHT_SPACING = 10f;

    private Vector2f headerSize = new Vector2f(size.getX(), HEADER_THICKNESS);
    private Vector4f color, headerColor = new Vector4f(0.2f, 0.8f, 0.4f, 1f);
    private float nextAvailableHeight = headerSize.getY() + HEIGHT_SPACING;

    public Pane(Vector4f color)
    {
        super(new Vector2f(), new Vector2f());
        this.color = color;
    }

    @Override
    public void addChild(UIComponent child)
    {
        super.addChild(child);
        child.position.add(0f, nextAvailableHeight);
        child.size.setX(size.getX());
        nextAvailableHeight += child.size.getY() + HEIGHT_SPACING;
    }

    @Override
    public void setSize(float width, float height)
    {
        super.setSize(width, height);
        headerSize.set(width, HEADER_THICKNESS);
        for (UIComponent child : children)
        {
            child.setSize(width - WIDHT_PADDING * 2, child.size.getY());
            child.position.setX(WIDHT_PADDING);
        }
    }

    public Pane(Vector2f position, Vector2f size, Vector4f color)
    {
        super(position, size);
        this.color = color;
    }

    @Override
    protected void render(Vector2f worldPosition)
    {
        Renderer2D.drawQuad(worldPosition, size, color);
        Renderer2D.drawQuad(worldPosition, headerSize, headerColor);
    }
}
