package haraldr.ui;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Batch2D;
import haraldr.input.Input;
import haraldr.input.MouseButton;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

import java.util.LinkedHashMap;
import java.util.Map;

public class UIComponentList extends UIComponent
{
    private static final float PADDING = 0f, MARGIN = 0f;
    private final float LINE_HEIGHT;

    private float headerHeight;

    private TextLabel name;
    private Map<TextLabel, UIComponent> components = new LinkedHashMap<>();
    private float divider, nextListY;
    private boolean collapsed;

    public UIComponentList(UIContainer parent, int layerIndex, String name, Vector2f position, Vector2f size)
    {
        super(parent, layerIndex);
        LINE_HEIGHT = textBatch.getFont().getSize();
        headerHeight = LINE_HEIGHT;
        this.name = textBatch.createTextLabel(name, position, new Vector4f(1f));

        setPosition(Vector2f.add(position, new Vector2f(MARGIN)));
        setSize(Vector2f.add(size, new Vector2f(-2f * MARGIN, 0f)));
    }

    public void addComponent(String name, UIComponent component)
    {
        TextLabel label = textBatch.createTextLabel(name, Vector2f.add(position, new Vector2f(0f, nextListY)), new Vector4f(1f));
        components.put(label, component);
        if (divider < label.getPixelWidth())
        {
            divider = label.getPixelWidth();
        }
        nextListY += LINE_HEIGHT;
        size.setY(nextListY);
    }

    @Override
    public void setSize(Vector2f size)
    {
        super.setSize(new Vector2f(size.getX() - 2f * MARGIN, headerHeight + nextListY + PADDING));
        for (UIComponent component : components.values())
        {
            component.setSize(new Vector2f(size.getX() - divider - MARGIN * PADDING, 20f));
        }
        textBatch.refreshTextMeshData();
    }

    @Override
    public void setPosition(Vector2f position)
    {
        this.position.set(Vector2f.add(position, MARGIN));
        float nextY = headerHeight + PADDING + MARGIN;

        name.setPosition(this.position);
        for (Map.Entry<TextLabel, UIComponent> entry : components.entrySet())
        {
            entry.getKey().setPosition(Vector2f.add(position, new Vector2f(2f, nextY)));
            entry.getValue().setPosition(Vector2f.add(position, new Vector2f(divider + PADDING, nextY)));
            nextY += LINE_HEIGHT;
        }
        textBatch.refreshTextMeshData();
    }

    @Override
    public UIEventResult onEvent(Event event, Window window)
    {
        boolean requiresRedraw = false, consumed = false;
        if (event.eventType == EventType.MOUSE_PRESSED && Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
        {
            var mousePressedEvent = (MousePressedEvent) event;
            Vector2f mousePoint = new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos);
            if (Physics2D.pointInsideAABB(mousePoint, position, new Vector2f(size.getX(), headerHeight)))
            {
                requiresRedraw = true;
                consumed = true;
                collapsed = !collapsed;
                for (TextLabel textLabel : components.keySet())
                {
                    textLabel.setEnabled(!collapsed);
                }
                for (UIComponent component : components.values())
                {
                    component.setEnabled(!collapsed);
                }
                textBatch.refreshTextMeshData();
            }
        }
        return new UIEventResult(requiresRedraw, consumed);
    }

    @Override
    public void draw(Batch2D batch)
    {
        if (!collapsed)
        {
            batch.drawQuad(position, size, new Vector4f(0.3f, 0.3f, 0.3f, 1f));
            components.values().forEach(component -> component.draw(batch));
        }
        batch.drawQuad(position, new Vector2f(size.getX(), headerHeight), new Vector4f(0.15f, 0.15f, 0.15f, 1f));
    }

    @Override
    public float getVerticalSize()
    {
        return collapsed ? headerHeight : size.getY();
    }
}