package haraldr.ui.components;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Batch2D;
import haraldr.input.Input;
import haraldr.input.MouseButton;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;
import haraldr.ui.TextLabel;

public class UIDropDownMenu extends UIComponent
{
    private static final float MENU_BUTTON_PADDING = 10f;

    private TextLabel name;
    private boolean hovered;
    private UIVerticalList actions;

    public UIDropDownMenu(UIContainer parent, int layerIndex, String name, Vector2f position, ListData[] listDataEntries)
    {
        super(parent, layerIndex);
        this.name = textBatch.createTextLabel(name, Vector2f.addX(position, MENU_BUTTON_PADDING / 2f), new Vector4f(1f));
        setPosition(position);
        setSize(new Vector2f(this.name.getPixelWidth() + MENU_BUTTON_PADDING, textBatch.getFont().getSize()));

        actions = new UIVerticalList(this, 0, new Vector4f(0.4f, 0.4f, 0.4f, 1f));
        float widestEntry = 0f;
        for (ListData listDataEntry : listDataEntries)
        {
            actions.addItem(listDataEntry.name(), listDataEntry.listItemCallback());
            float labelWidth = this.name.getFont().getPixelWidth(listDataEntry.name());
            if (labelWidth > widestEntry) widestEntry = labelWidth;
        }
        actions.setPosition(Vector2f.addY(position, size.getY()));
        actions.setSize(new Vector2f(widestEntry, 0f));
        actions.setEnabled(false);
    }

    @Override
    public UIEventResult onEvent(Event event, Window window)
    {
        boolean requiresRedraw = false;
        if (event.eventType == EventType.MOUSE_PRESSED && Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
        {
            var mousePressedEvent = (MousePressedEvent) event;
            boolean pressed = Physics2D.pointInsideAABB(new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos), position, size);
            if (pressed)
            {
                actions.setEnabled(!actions.isEnabled());
                requiresRedraw = true;
            }
        }
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            var mouseMovedEvent = (MouseMovedEvent) event;
            boolean previousHoveredState = hovered;
            hovered = Physics2D.pointInsideAABB(new Vector2f(mouseMovedEvent.xPos, mouseMovedEvent.yPos), position, size);
            requiresRedraw = previousHoveredState != hovered;
        }
        UIEventResult eventResult = actions.onEvent(event, window);
        requiresRedraw |= eventResult.requiresRedraw();
        return new UIEventResult(requiresRedraw, eventResult.consumed());
    }

    @Override
    public void draw(Batch2D batch)
    {
        batch.drawQuad(position, size, hovered ? new Vector4f(0.6f, 0.6f, 0.6f, 1f) : new Vector4f(0.4f, 0.4f, 0.4f, 1f));
        actions.draw(batch);
    }

    public TextLabel getName()
    {
        return name;
    }
}
