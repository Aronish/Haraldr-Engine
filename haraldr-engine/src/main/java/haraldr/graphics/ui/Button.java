package haraldr.graphics.ui;

import haraldr.graphics.Renderer2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class Button extends UIComponent
{
    public static final Vector4f ON_COLOR = new Vector4f(0.2f, 0.8f, 0.3f, 1f);
    public static final Vector4f OFF_COLOR = new Vector4f(0.8f, 0.2f, 0.3f, 1f);

    private boolean active;

    public Button(Vector2f position, Vector2f size)
    {
        super(position, size);
    }

    public void onClick(int x, int y)
    {
        if (x >= position.getX() && x <= position.getX() + size.getX() && y >= position.getY() && y <= position.getY() + size.getY())
        {
            active = !active;
        }
    }

    @Override
    protected void render(Vector2f worldPosition)
    {
        Renderer2D.drawQuad(worldPosition, size, active ? ON_COLOR : OFF_COLOR);
    }
}
