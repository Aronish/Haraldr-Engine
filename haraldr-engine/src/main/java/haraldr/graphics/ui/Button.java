package haraldr.graphics.ui;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class Button extends LabeledComponent
{
    private static final Vector4f ON_COLOR = new Vector4f(0.2f, 0.8f, 0.3f, 1f);
    private static final Vector4f OFF_COLOR = new Vector4f(0.8f, 0.2f, 0.3f, 1f);

    private Vector2f buttonPosition, buttonSize;
    private boolean active;
    private ButtonPressAction buttonPressAction;

    public Button(String name, Pane parent)
    {
        this(name, parent, () -> {});
    }

    public Button(String name, Pane parent, ButtonPressAction buttonPressAction)
    {
        super(name, parent);
        buttonPosition = Vector2f.add(position, new Vector2f(0f, label.getPixelWidth()));
        buttonSize = new Vector2f(parent.size.getX() - parent.getDivider(), label.getFont().getSize());
        this.buttonPressAction = buttonPressAction;
    }

    public void setPressAction(ButtonPressAction action)
    {
        this.buttonPressAction = action;
    }

    @Override
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        buttonPosition.set(Vector2f.add(position, new Vector2f(parent.getDivider(), 0f)));
    }

    @Override
    public void onEvent(Event event)
    {
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            var mousePressedEvent = (MousePressedEvent) event;
            if (mousePressedEvent.xPos >= buttonPosition.getX() &&
                mousePressedEvent.xPos <= buttonPosition.getX() + buttonSize.getX() &&
                mousePressedEvent.yPos >= buttonPosition.getY() &&
                mousePressedEvent.yPos <= buttonPosition.getY() + buttonSize.getY())
            {
                active = true;
                buttonPressAction.run();
            }
        }
        if (event.eventType == EventType.MOUSE_RELEASED)
        {
            active = false;
        }
    }

    @Override
    public void onUpdate(float deltaTime)
    {
    }

    @Override
    public void render()
    {
        Renderer2D.drawQuad(buttonPosition, buttonSize, active ? ON_COLOR : OFF_COLOR);
    }

    @Override
    public float getVerticalSize()
    {
        return buttonSize.getY();
    }

    @FunctionalInterface
    public interface ButtonPressAction
    {
        void run();
    }
}
