package haraldr.dockspace.uicomponents;

import haraldr.dockspace.DockablePanel;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.event.ParentCollapsedEvent;
import haraldr.graphics.Batch2D;
import haraldr.input.Input;
import haraldr.input.MouseButton;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

import java.util.LinkedHashMap;
import java.util.Map;

public class ComponentPropertyList
{
    private static final float PADDING = 0f, MARGIN = 0f;
    private final float LINE_HEIGHT;

    private DockablePanel parent;
    private Vector2f position, size, headerSize;

    private TextLabel name;
    private Map<TextLabel, UIComponent> components = new LinkedHashMap<>();
    private float divider, nextListY;
    private boolean collapsed;

    public ComponentPropertyList(String name, DockablePanel parent)
    {
        this.parent = parent;
        position = Vector2f.add(parent.getPosition(), new Vector2f(MARGIN));
        size = Vector2f.add(parent.getSize(), new Vector2f(-2f * MARGIN, 0f));
        headerSize = new Vector2f(size.getX(), parent.getHeaderHeight());
        this.name = parent.getTextBatch().createTextLabel(name, position, new Vector4f(1f));
        LINE_HEIGHT = parent.getTextBatch().getFont().getSize();
    }

    public boolean onEvent(Event event)
    {
        boolean requireRedraw = false;
        if (event.eventType == EventType.MOUSE_PRESSED && Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
        {
            var mousePressedEvent = (MousePressedEvent) event;
            Vector2f mousePoint = new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos);
            if (Physics2D.pointInsideAABB(mousePoint, position, headerSize))
            {
                requireRedraw = true;
                collapsed = !collapsed;
                for (TextLabel textLabel : components.keySet())
                {
                    textLabel.setEnabled(!collapsed);
                }
                for (UIComponent component : components.values())
                {
                    component.onEvent(new ParentCollapsedEvent(!collapsed));
                }
                parent.getTextBatch().refreshTextMeshData();
            }
        }

        if (!collapsed)
        {
            for (UIComponent component : components.values())
            {
                if (component.onEvent(event)) requireRedraw = true;
            }
        }
        return requireRedraw;
    }

    public void addComponent(String name, UIComponent component)
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
        this.position.set(Vector2f.add(position, MARGIN));
        float nextY = headerSize.getY() + PADDING + MARGIN;

        name.setPosition(this.position);
        for (Map.Entry<TextLabel, UIComponent> entry : components.entrySet())
        {
            entry.getKey().setPosition(Vector2f.add(position, new Vector2f(2f, nextY)));
            entry.getValue().setPosition(Vector2f.add(position, new Vector2f(divider + PADDING, nextY)));
            nextY += LINE_HEIGHT;
        }
        parent.getTextBatch().refreshTextMeshData();
    }

    public void addPosition(Vector2f position)
    {
        this.position.add(position);
        name.addPosition(position);
        for (Map.Entry<TextLabel, UIComponent> entry : components.entrySet())
        {
            entry.getKey().addPosition(position);
            entry.getValue().addPosition(position);
        }
        parent.getTextBatch().refreshTextMeshData();
    }

    public void setSize(Vector2f size)
    {
        this.size.set(size.getX() - 2f * MARGIN, headerSize.getY() + nextListY + PADDING);
        headerSize.set(this.size.getX(), parent.getHeaderHeight());
        for (UIComponent component : components.values())
        {
            component.setWidth(size.getX() - divider - MARGIN * PADDING);
        }
        parent.getTextBatch().refreshTextMeshData();
    }

    public void render(Batch2D batch)
    {
        if (!collapsed)
        {
            batch.drawQuad(position, size, new Vector4f(0.3f, 0.3f, 0.3f, 1f));
            components.values().forEach(component -> component.render(batch));
        }
        batch.drawQuad(position, headerSize, new Vector4f(0.15f, 0.15f, 0.15f, 1f));
    }

    public DockablePanel getParent()
    {
        return parent;
    }

    public Vector2f getSize()
    {
        return collapsed ? headerSize : size;
    }
}