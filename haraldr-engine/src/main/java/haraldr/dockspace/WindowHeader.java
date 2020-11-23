package haraldr.dockspace;

import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class WindowHeader
{
    private Vector2f position, size;
    private Vector4f color;

    public WindowHeader(Vector2f position, float size, Vector4f color)
    {
        this.position = position;
        this.size = new Vector2f(size, 20f);
        this.color = color;
    }

    public void render(Batch2D batch)
    {
        batch.drawQuad(position, size, color);
    }

    public Vector2f getSize()
    {
        return size;
    }
}