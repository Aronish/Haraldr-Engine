package haraldr.dockspace.uicomponents;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Batch2D;
import haraldr.input.Input;
import haraldr.input.MouseButton;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

public class UnlabeledCheckbox extends UnlabeledComponent
{
    private static final Vector4f OFF_COLOR = new Vector4f(0.8f, 0.2f, 0.3f, 1f), ON_COLOR = new Vector4f(0.3f, 0.8f, 0.2f, 1f);

    private Vector2f size;
    private boolean state;

    private Checkbox.CheckboxStateChangeAction checkboxStateChangeAction;

    public UnlabeledCheckbox()
    {
        this((state) -> {});
    }

    public UnlabeledCheckbox(Checkbox.CheckboxStateChangeAction checkboxStateChangeAction)
    {
        size = new Vector2f(20f);
        this.checkboxStateChangeAction = checkboxStateChangeAction;
    }

    public void setCheckboxStateChangeAction(Checkbox.CheckboxStateChangeAction checkboxStateChangeAction)
    {
        this.checkboxStateChangeAction = checkboxStateChangeAction;
    }

    @Override
    public void setWidth(float width)
    {
        size.setX(width);
    }

    @Override
    public boolean onEvent(Event event)
    {
        boolean requireRedraw = false;
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
            {
                var mousePressedEvent = (MousePressedEvent) event;
                if (Physics2D.pointInsideAABB(new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos), position, size))
                {
                    state = !state;
                    checkboxStateChangeAction.run(state);
                    requireRedraw = true;
                }
            }
        }
        return requireRedraw;
    }

    @Override
    public void render(Batch2D batch)
    {
        batch.drawQuad(position, size, state ? ON_COLOR : OFF_COLOR);
    }

    @Override
    public float getVerticalSize()
    {
        return size.getY();
    }
}
