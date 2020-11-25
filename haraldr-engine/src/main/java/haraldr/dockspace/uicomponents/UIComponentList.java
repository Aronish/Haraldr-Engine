package haraldr.dockspace.uicomponents;

import haraldr.dockspace.DockablePanel;
import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

import java.util.HashMap;
import java.util.Map;

public class UIComponentList
{
    private static final float LINE_HEIGHT = 25f;

    private DockablePanel parent;
    private Vector2f position, size;

    private Map<TextLabel, UnLabeledComponent> components = new HashMap<>();
    private float divider, nextListY;

    public UIComponentList(DockablePanel parent)
    {
        this.parent = parent;
        position = new Vector2f(parent.getPosition());
        size = new Vector2f(parent.getSize().getX(), 0f);
    }

    public void addComponent(String name, UnLabeledComponent component)
    {
        TextLabel label = parent.getTextBatch().createTextLabel(name, Vector2f.add(parent.getPosition(), new Vector2f(0f, nextListY)), new Vector4f(1f));
        components.put(label, component);
        if (divider < label.getPixelWidth())
        {
            divider = label.getPixelWidth();
        }
        nextListY += LINE_HEIGHT;
        size.setY(nextListY);
    }

    public void setPosition(Vector2f position)
    {
        for (int i = 0; i < components.size(); ++i)
        {

        }
        this.position.set(position);
    }

    public void setSize(Vector2f size)
    {
        this.size.set(size.getX(), nextListY);
    }

    public void render(Batch2D batch)
    {
        batch.drawQuad(position, size, new Vector4f(0.8f, 0.2f, 0.3f, 1f));
    }
}