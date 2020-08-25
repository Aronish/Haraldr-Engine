package haraldr.graphics.ui;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.input.Button;
import haraldr.input.Input;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class Checkbox extends LabeledComponent
{
    private static final Vector4f OFF_COLOR = new Vector4f(0.8f, 0.2f, 0.3f, 1f), ON_COLOR = new Vector4f(0.3f, 0.8f, 0.2f, 1f);

    private Vector2f boxPosition = new Vector2f(), boxSize;
    private boolean state;

    private CheckboxStateChangeAction checkboxStateChangeAction;

    public Checkbox(String name, Pane parent)
    {
        this(name, parent, (state) -> {});
    }

    public Checkbox(String name, Pane parent, CheckboxStateChangeAction checkboxStateChangeAction)
    {
        super(name, parent);
        boxSize = new Vector2f(parent.getComponentDivisionSize(), label.getFont().getSize());
        this.checkboxStateChangeAction = checkboxStateChangeAction;
    }

    @Override
    public void setComponentPosition(Vector2f position)
    {
        boxPosition = position;
    }

    @Override
    public void setWidth(float width)
    {
        boxSize.setX(width);
    }

    public void setCheckboxStateChangeAction(CheckboxStateChangeAction checkboxStateChangeAction)
    {
        this.checkboxStateChangeAction = checkboxStateChangeAction;
    }

    @Override
    public void onEvent(Event event)
    {
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            if (Input.wasMouseButton(event, Button.MOUSE_BUTTON_1))
            {
                var mousePressedEvent = (MousePressedEvent) event;
                if (mousePressedEvent.xPos > boxPosition.getX() &&
                    mousePressedEvent.xPos < boxPosition.getX() + boxSize.getX() &&
                    mousePressedEvent.yPos > boxPosition.getY() &&
                    mousePressedEvent.yPos < boxPosition.getY() + boxSize.getY())
                {
                    state = !state;
                    checkboxStateChangeAction.run(state);
                }
            }
        }
    }

    @Override
    public void onUpdate(float deltaTime)
    {
    }

    @Override
    public void render()
    {
        Renderer2D.drawQuad(boxPosition, boxSize, state ? ON_COLOR : OFF_COLOR);
    }

    @Override
    public float getVerticalSize()
    {
        return boxSize.getY();
    }

    @FunctionalInterface
    public interface CheckboxStateChangeAction
    {
        void run(boolean state);
    }
}
