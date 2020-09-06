package haraldr.graphics.ui;

import haraldr.event.Event;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

// These are to be contained in a Pane, they cannot be freely positioned.
public abstract class LabeledComponent
{
    private static final Vector4f ENABLED_COLOR = new Vector4f(1f), DISABLED_COLOR = new Vector4f(0.7f, 0.7f, 0.7f, 1f);

    protected Pane parent;

    protected Vector2f position = new Vector2f();
    protected TextLabel label;
    protected boolean enabled = true;

    public LabeledComponent(String name, Pane parent)
    {
        this.parent = parent;
        label = parent.textBatch.createTextLabel(name, position, ENABLED_COLOR);
    }

    public void setPosition(Vector2f position, float divider)
    {
        this.position.set(position);
        label.setPosition(position);
        parent.textBatch.refreshTextMeshData();
        setComponentPosition(Vector2f.add(position, new Vector2f(divider + parent.getSidePadding(), 0f)));
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        label.setColor(this.enabled ? ENABLED_COLOR : DISABLED_COLOR);
        parent.textBatch.refreshTextMeshData();
    }

    protected abstract void setComponentPosition(Vector2f position);

    public abstract void setWidth(float width);

    public abstract float getVerticalSize();

    public abstract void onEvent(Event event);

    public abstract void onUpdate(float deltaTime);

    public abstract void render();
}