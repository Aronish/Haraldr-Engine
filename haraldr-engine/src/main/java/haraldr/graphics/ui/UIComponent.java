package haraldr.graphics.ui;

import haraldr.math.Vector2f;

import java.util.ArrayList;
import java.util.List;

public abstract class UIComponent
{
    protected Vector2f position, size;

    protected List<UIComponent> children = new ArrayList<>();

    public UIComponent(Vector2f position, Vector2f size)
    {
        this.position = position;
        this.size = size;
    }

    public void addChild(UIComponent child)
    {
        children.add(child);
    }

    public void setPosition(float x, float y)
    {
        position.set(x, y);
    }

    public void setSize(float width, float height)
    {
        size.set(width, height);
    }

    public Vector2f getPosition()
    {
        return position;
    }

    public Vector2f getSize()
    {
        return size;
    }

    protected abstract void render(Vector2f worldPosition);

    public void renderAll(Vector2f parentPosition)
    {
        render(Vector2f.add(parentPosition, position));
        for (UIComponent child : children)
        {
            child.renderAll(Vector2f.add(parentPosition, position));
        }
    }
}
