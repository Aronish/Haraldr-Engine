package haraldr.ui.components;

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

public class UICheckbox extends UIComponent
{
    private static final Vector4f OFF_COLOR = new Vector4f(0.8f, 0.2f, 0.3f, 1f), ON_COLOR = new Vector4f(0.3f, 0.8f, 0.2f, 1f);

    private boolean state;

    private CheckboxStateChangeAction checkboxStateChangeAction;

    public UICheckbox(UIContainer parent, int layerIndex)
    {
        this(parent, layerIndex, false, state -> {});
    }

    public UICheckbox(UIContainer parent, int layerIndex, boolean initialState)
    {
        this(parent, layerIndex, initialState, state -> {});
    }

    public UICheckbox(UIContainer parent, int layerIndex, boolean initialState, CheckboxStateChangeAction checkboxStateChangeAction)
    {
        super(parent, layerIndex);
        setSize(new Vector2f(20f));
        state = initialState;
        this.checkboxStateChangeAction = checkboxStateChangeAction;
    }

    public void setCheckboxStateChangeAction(CheckboxStateChangeAction checkboxStateChangeAction)
    {
        this.checkboxStateChangeAction = checkboxStateChangeAction;
    }

    @Override
    public UIEventResult onEvent(Event event, Window window)
    {
        boolean requiresRedraw = false;
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            if (Input.wasMousePressed(event, MouseButton.MOUSE_BUTTON_1))
            {
                var mousePressedEvent = (MousePressedEvent) event;
                if (Physics2D.pointInsideAABB(new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos), position, size))
                {
                    state = !state;
                    checkboxStateChangeAction.run(state);
                    requiresRedraw = true;
                }
            }
        }
        return new UIEventResult(requiresRedraw, false);
    }

    @Override
    public void draw(Batch2D batch)
    {
        batch.drawQuad(position, size, state ? ON_COLOR : OFF_COLOR);
    }

    public boolean isChecked()
    {
        return state;
    }

    @FunctionalInterface
    public interface CheckboxStateChangeAction
    {
        void run(boolean state);
    }
}
