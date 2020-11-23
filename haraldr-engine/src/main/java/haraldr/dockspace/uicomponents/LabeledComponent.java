package haraldr.dockspace.uicomponents;

import haraldr.dockspace.ControlPanel;
import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public abstract class LabeledComponent
{
    private static final Vector4f ENABLED_COLOR = new Vector4f(1f), DISABLED_COLOR = new Vector4f(0.7f, 0.7f, 0.7f, 1f);

    protected ControlPanel parent;

    protected Vector2f position = new Vector2f();
    protected TextLabel label;
    protected boolean enabled = true;

    public LabeledComponent(String name, ControlPanel parent)
    {
        this.parent = parent;
        label = parent.getTextBatch().createTextLabel(name, position, ENABLED_COLOR);
    }

    public void setPosition(Vector2f position, float divider)
    {
        this.position.set(position);
        label.setPosition(position);
        parent.getTextBatch().refreshTextMeshData();
        setComponentPosition(Vector2f.add(position, new Vector2f(divider + parent.getSidePadding(), 0f)));
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        label.setColor(this.enabled ? ENABLED_COLOR : DISABLED_COLOR);
        parent.getTextBatch().refreshTextMeshData();
    }

    public float getLabelWidth()
    {
        return label.getPixelWidth();
    }

    protected abstract void setComponentPosition(Vector2f position);

    public abstract void setWidth(float width);

    public abstract float getVerticalSize();

    /**
     * @return true if an event requires a ControlPanel to redraw.
     */
    public abstract boolean onEvent(Event event);

    public abstract void render(Batch2D batch);
}