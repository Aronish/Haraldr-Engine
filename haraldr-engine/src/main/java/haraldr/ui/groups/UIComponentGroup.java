package haraldr.ui.groups;

import haraldr.math.Vector2f;
import haraldr.ui.components.UIPositionable;

public abstract class UIComponentGroup<InsertData> implements UIPositionable
{
    protected Vector2f position = new Vector2f(), size = new Vector2f();

    public abstract void addComponent(InsertData insertData);

    @Override
    public void setPosition(Vector2f position)
    {
        this.position.set(position);
    }

    @Override
    public void setSize(Vector2f size)
    {
        this.size.set(size);
    }

    @Override
    public Vector2f getPosition()
    {
        return position;
    }

    @Override
    public Vector2f getSize()
    {
        return size;
    }

    @Override
    public float getVerticalSize()
    {
        return size.getY();
    }
}