package haraldr.ui.components;

import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.ui.TextLabel;

import java.util.LinkedHashMap;
import java.util.Map;

public class UILabeledList extends UIComponent
{
    private static final float PADDING = 0f, MARGIN = 0f;
    private final float LINE_HEIGHT;

    private Map<TextLabel, UIComponent> components = new LinkedHashMap<>();
    private float divider, nextListY;

    public UILabeledList(UIContainer parent, int layerIndex, Vector2f position, Vector2f size)
    {
        super(parent, layerIndex);
        LINE_HEIGHT = textBatch.getFont().getSize();

        setPosition(Vector2f.add(position, MARGIN));
        setSize(Vector2f.addX(size, -2f * MARGIN));
    }

    public void addComponent(String name, UIComponent component)
    {
        TextLabel label = textBatch.createTextLabel(name, Vector2f.addY(position, nextListY), new Vector4f(1f));
        components.put(label, component);
        if (divider < label.getPixelWidth())
        {
            divider = label.getPixelWidth();
        }
        nextListY += LINE_HEIGHT;
        size.setY(nextListY);
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        for (TextLabel textLabel : components.keySet())
        {
            textLabel.setEnabled(enabled);
        }
        for (UIComponent component : components.values())
        {
            component.setEnabled(enabled);
        }
        textBatch.refreshTextMeshData();
    }

    @Override
    public void setSize(Vector2f size)
    {
        super.setSize(new Vector2f(size.getX() - 2f * MARGIN, nextListY + PADDING));
        for (UIComponent component : components.values())
        {
            component.setSize(new Vector2f(size.getX() - divider - MARGIN * PADDING, LINE_HEIGHT));
        }
        textBatch.refreshTextMeshData();
    }

    @Override
    public void setPosition(Vector2f position)
    {
        this.position.set(Vector2f.add(position, MARGIN));
        float nextY = PADDING + MARGIN;

        for (Map.Entry<TextLabel, UIComponent> entry : components.entrySet())
        {
            entry.getKey().setPosition(Vector2f.add(position, new Vector2f(2f, nextY)));
            entry.getValue().setPosition(Vector2f.add(position, new Vector2f(divider + PADDING, nextY)));
            nextY += LINE_HEIGHT;
        }
        textBatch.refreshTextMeshData();
    }

    @Override
    public void draw(Batch2D batch)
    {
        if (enabled)
        {
            batch.drawQuad(position, size, new Vector4f(0.3f, 0.3f, 0.3f, 1f));
            components.values().forEach(component -> component.draw(batch));
        }
    }
}