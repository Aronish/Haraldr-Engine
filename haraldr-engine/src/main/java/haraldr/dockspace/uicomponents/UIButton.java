package haraldr.dockspace.uicomponents;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

public class UIButton extends UIComponent
{
    private static final Vector4f ON_COLOR = new Vector4f(0.2f, 0.8f, 0.3f, 1f);
    private static final Vector4f OFF_COLOR = new Vector4f(0.8f, 0.2f, 0.3f, 1f);

    private Vector2f buttonSize;
    private Vector4f currentColor = OFF_COLOR;
    private ButtonPressAction buttonPressAction;

    public UIButton()
    {
        this(() -> {});
    }

    public UIButton(ButtonPressAction buttonPressAction)
    {
        buttonSize = new Vector2f(20f);
        this.buttonPressAction = buttonPressAction;
    }

    public void setPressAction(ButtonPressAction action)
    {
        this.buttonPressAction = action;
    }

    @Override
    public void setWidth(float width)
    {
        buttonSize.setX(width);
    }

    @Override
    public boolean onEvent(Event event)
    {
        boolean requireRedraw = false;
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            var mousePressedEvent = (MousePressedEvent) event;
            if (Physics2D.pointInsideAABB(new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos), position, buttonSize))
            {
                currentColor = ON_COLOR;
                buttonPressAction.run();
                requireRedraw = true;
            }
        }
        if (event.eventType == EventType.MOUSE_RELEASED)
        {
            requireRedraw = currentColor != OFF_COLOR;
            currentColor = OFF_COLOR;
        }
        return requireRedraw;
    }

    @Override
    public void render(Batch2D batch)
    {
        batch.drawQuad(position, buttonSize, currentColor);
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
