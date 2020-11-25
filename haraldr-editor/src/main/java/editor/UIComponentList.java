package editor;

import haraldr.dockspace.DockablePanel;
import haraldr.dockspace.uicomponents.TextLabel;
import haraldr.dockspace.uicomponents.UnlabeledComponent;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Batch2D;
import haraldr.input.Input;
import haraldr.input.MouseButton;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

import java.util.LinkedHashMap;
import java.util.Map;

public class UIComponentList
{
    private static final float LINE_HEIGHT = 25f, PADDING = 5f, MARGIN = 2f;

    private DockablePanel parent;
    private Vector2f position, size, headerSize;

    private TextLabel name;
    private Map<TextLabel, UnlabeledComponent> components = new LinkedHashMap<>();
    private float divider, nextListY;
    private boolean collapsed;

    public UIComponentList(String name, DockablePanel parent)
    {
        this.parent = parent;
        position = Vector2f.add(parent.getPosition(), new Vector2f(MARGIN));
        size = Vector2f.add(parent.getSize(), new Vector2f(-2f * MARGIN, 0f));
        headerSize = new Vector2f(size.getX(), parent.getHeaderHeight());
        this.name = parent.getTextBatch().createTextLabel(name, position, new Vector4f(1f));
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
                parent.getTextBatch().refreshTextMeshData();
            }
        }

        for (UnlabeledComponent unlabeledComponent : components.values())
        {
            if (unlabeledComponent.onEvent(event)) requireRedraw = true;
        }
        return requireRedraw;
    }

    public void addComponent(String name, UnlabeledComponent component)
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
        for (Map.Entry<TextLabel, UnlabeledComponent> entry : components.entrySet())
        {
            entry.getKey().setPosition(Vector2f.add(position, new Vector2f(2f, nextY)));
            entry.getValue().setPosition(Vector2f.add(position, new Vector2f(divider + PADDING, nextY)));
            nextY += LINE_HEIGHT;
        }
    }

    public void setSize(Vector2f size)
    {
        this.size.set(size.getX() - 2f * MARGIN, headerSize.getY() + nextListY + PADDING);
        headerSize.set(this.size.getX(), parent.getHeaderHeight());
        for (UnlabeledComponent unlabeledComponent : components.values())
        {
            unlabeledComponent.setWidth(size.getX() - divider - MARGIN * PADDING);
        }
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
}