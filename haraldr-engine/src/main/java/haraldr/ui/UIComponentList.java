package haraldr.ui;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.event.ParentCollapsedEvent;
import haraldr.graphics.Batch2D;
import haraldr.graphics.TextBatchContainer;
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

    private Vector2f size, headerSize;

    private TextLabel name;
    private Map<TextLabel, UIComponent> components = new LinkedHashMap<>();
    private float divider, nextListY;
    private boolean collapsed;

    public UIComponentList(TextBatchContainer parent, String name, Vector2f position, Vector2f size)
    {
        super(parent);
        this.position = Vector2f.add(position, new Vector2f(MARGIN));
        this.size = Vector2f.add(size, new Vector2f(-2f * MARGIN, 0f));
        LINE_HEIGHT = textBatch.getFont().getSize();
        headerSize = new Vector2f(size.getX(), LINE_HEIGHT);
        this.name = textBatch.createTextLabel(name, position, new Vector4f(1f));
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
    public void setWidth(float width)
    {
        size.set(width - 2f * MARGIN, headerSize.getY() + nextListY + PADDING);
        headerSize.set(width, LINE_HEIGHT);
        for (UIComponent component : components.values())
        {
            component.setWidth(width - divider - MARGIN * PADDING);
        }
        textBatch.refreshTextMeshData();
    }

    @Override
    public void setPosition(Vector2f position)
    {
        this.position.set(Vector2f.add(position, MARGIN));
        float nextY = headerSize.getY() + PADDING + MARGIN;

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
    public void addPosition(Vector2f position)
    {
        this.position.add(position);
        name.addPosition(position);
        for (Map.Entry<TextLabel, UIComponent> entry : components.entrySet())
        {
            entry.getKey().addPosition(position);
            entry.getValue().addPosition(position);
        }
        textBatch.refreshTextMeshData();
    }

    @Override
    public boolean onEvent(Event event, Window window)
    {
        boolean requiresRedraw = false;
        if (event.eventType == EventType.MOUSE_PRESSED && Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
        {
            var mousePressedEvent = (MousePressedEvent) event;
            Vector2f mousePoint = new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos);
            if (Physics2D.pointInsideAABB(mousePoint, position, headerSize))
            {
                requiresRedraw = true;
                collapsed = !collapsed;
                for (TextLabel textLabel : components.keySet())
                {
                    textLabel.setEnabled(!collapsed);
                }
                for (UIComponent component : components.values())
                {
                    component.onEvent(new ParentCollapsedEvent(collapsed), window);
                }
                textBatch.refreshTextMeshData();
            }
        }

        if (!collapsed)
        {
            for (UIComponent component : components.values())
            {
                if (component.onEvent(event, window)) requiresRedraw = true;
            }
        }
        return requiresRedraw;
    }

    @Override
    public void draw(Batch2D batch)
    {
        if (!collapsed)
        {
            batch.drawQuad(position, size, new Vector4f(0.3f, 0.3f, 0.3f, 1f));
            components.values().forEach(component -> component.draw(batch));
        }
        batch.drawQuad(position, headerSize, new Vector4f(0.15f, 0.15f, 0.15f, 1f));
    }

    @Override
    public void onDispose()
    {
        for (UIComponent uiComponent : components.values())
        {
            uiComponent.onDispose();
        }
    }

    @Override
    public float getVerticalSize()
    {
        return collapsed ? headerSize.getY() : size.getY();
    }
}