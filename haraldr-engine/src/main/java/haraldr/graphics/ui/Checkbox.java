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

    private CheckboxStateChangeAction stateChangeAction = (state) -> {};

    public Checkbox(String name, Pane parent)
    {
        super(name, parent);
        boxSize = new Vector2f(parent.size.getX() - parent.getDivider(), label.getFont().getSize());
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        boxPosition.set(Vector2f.add(position, new Vector2f(parent.getDivider(), 0f)));
    }

    public void setStateChangeAction(CheckboxStateChangeAction stateChangeAction)
    {
        this.stateChangeAction = stateChangeAction;
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
                    stateChangeAction.run(state);
                }
            }
        }
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
