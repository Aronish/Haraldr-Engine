package haraldr.graphics.ui;

import haraldr.event.Event;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

// These are to be contained in a Pane, they cannot be freely positioned.
public abstract class LabeledComponent
{
    protected Pane parent;

    protected Vector2f position = new Vector2f();
    protected TextLabel label;

    public LabeledComponent(String name, Pane parent)
    {
        this.parent = parent;
        label = parent.textBatch.createTextLabel(name, position, new Vector4f(1f));
    }

    public void setPosition(Vector2f position, float divider)
    {
        this.position.set(position);
        label.setPosition(position);
        parent.textBatch.refreshTextMeshData();
        setComponentPosition(Vector2f.add(position, new Vector2f(divider, 0f)));
    }

    protected abstract void setComponentPosition(Vector2f position);

    public abstract void setWidth(float width);

    public abstract float getVerticalSize();

    public abstract void onEvent(Event event);

    public abstract void onUpdate(float deltaTime);

    public abstract void render();
}