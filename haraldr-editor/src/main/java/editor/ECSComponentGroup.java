package editor;

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
import haraldr.ui.TextLabel;
import haraldr.ui.components.UIComponent;
import haraldr.ui.components.UIContainer;
import haraldr.ui.components.UILabeledList;

public class ECSComponentGroup extends UIComponent
{
    private TextLabel name;
    private boolean collapsed;
    private float headerHeight;
    private UILabeledList componentList;

    public ECSComponentGroup(UIContainer parent, int layerIndex, String name, Vector2f position, Vector2f size)
    {
        super(parent, layerIndex);
        headerHeight = textBatch.getFont().getSize();
        this.name = textBatch.createTextLabel(name, position, new Vector4f(1f));
        componentList = new UILabeledList(this, 0, position, size);

        setPosition(position);
        setSize(size);
    }

    @Override
    public void setSize(Vector2f size)
    {
        super.setSize(size);
        componentList.setSize(size);
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        name.setPosition(position);
        componentList.setPosition(Vector2f.addY(position, headerHeight));
    }

    @Override
    public UIEventResult onEvent(Event event, Window window)
    {
        UIEventResult eventResult = componentList.onEvent(event, window);
        boolean requiresRedraw = eventResult.requiresRedraw(), consumed = eventResult.consumed();

        if (event.eventType == EventType.MOUSE_PRESSED && Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
        {
            var mousePressedEvent = (MousePressedEvent) event;
            Vector2f mousePoint = new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos);
            if (Physics2D.pointInsideAABB(mousePoint, position, new Vector2f(size.getX(), headerHeight)))
            {
                requiresRedraw = true;
                consumed = true;
                collapsed = !collapsed;
                componentList.setEnabled(!collapsed);
            }
        }
        return new UIEventResult(requiresRedraw, consumed);
    }

    @Override
    public void draw(Batch2D batch)
    {
        componentList.draw(batch);
        batch.drawQuad(position, new Vector2f(size.getX(), headerHeight), new Vector4f(0.15f, 0.15f, 0.15f, 1f));
    }

    @Override
    public float getVerticalSize()
    {
        return collapsed ? headerHeight : headerHeight + componentList.getVerticalSize();
    }

    public UILabeledList getComponentList()
    {
        return componentList;
    }
}