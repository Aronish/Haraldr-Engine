package haraldr.graphics.ui;

import haraldr.event.Event;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public abstract class UIComponent
{
    protected static final Font DEFAULT_FONT = new Font("default_fonts/consola.ttf", 16);

    protected Vector2f position, size;
    protected TextBatch textBatch = new TextBatch(DEFAULT_FONT);
    protected TextLabel name;

    public UIComponent(Vector2f position, Vector2f size, String name)
    {
        this.position = position;
        this.size = size;
        this.name = textBatch.createTextLabel(name, position, new Vector4f(0f, 0f, 0f, 1f));
    }

    protected abstract void setupLabel(String name);

    public void setPosition(Vector2f position)
    {
        this.position.set(position);
    }

    public void setSize(int width, int height)
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

    public abstract void onEvent(Event event);

    public abstract void render(Vector2f parentPosition);

    public void renderText()
    {
        textBatch.render();
    }
}
