package haraldr.graphics.ui;

import haraldr.graphics.Renderer2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class Pane extends Container
{
    private Vector2f headerSize = new Vector2f(size.getX(), 30f);
    private Vector4f color, headerColor = new Vector4f(0.2f, 0.8f, 0.4f, 1f);

    public Pane(Vector4f color, GridLayout layout)
    {
        this(new Vector2f(), new Vector2f(), color, layout);
    }

    public Pane(Vector2f position, Vector2f size, Vector4f color, GridLayout layout)
    {
        super(position, size, layout);
        this.color = color;
    }

    @Override
    public void setSize(int width, int height)
    {
        super.setSize(width, height);
        headerSize.setX(width);
    }



    @Override
    public void renderSelf(Vector2f screenPosition)
    {
        Renderer2D.drawQuad(screenPosition, size, color);
        Renderer2D.drawQuad(screenPosition, headerSize, headerColor);
    }
}
