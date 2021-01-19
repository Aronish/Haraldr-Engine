package haraldr.ui;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

public class UIButton extends UIComponent
{
    private static final Vector4f ON_COLOR = new Vector4f(0.2f, 0.8f, 0.3f, 1f);
    private static final Vector4f OFF_COLOR = new Vector4f(0.8f, 0.2f, 0.3f, 1f);
    private static final Vector4f DISABLED_COLOR = new Vector4f(0.4f, 0.4f, 0.4f, 1f);

    private Vector2f buttonSize;
    private Vector4f currentColor = OFF_COLOR;
    private ButtonPressAction buttonPressAction;

    public UIButton()
    {
        this(null, () -> {});
    }

    public UIButton(UIContainer parent, ButtonPressAction buttonPressAction)
    {
        super(parent);
        buttonSize = new Vector2f(20f);
        this.buttonPressAction = buttonPressAction;
    }

    public void setPressAction(ButtonPressAction action)
    {
        this.buttonPressAction = action;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        currentColor = enabled ? OFF_COLOR : DISABLED_COLOR;
    }

    @Override
    public void setWidth(float width)
    {
        buttonSize.setX(width);
    }

    @Override
    public UIEventResult onEvent(Event event, Window window)
    {
        boolean requiresRedraw = false;
        if (enabled)
        {
            if (event.eventType == EventType.MOUSE_PRESSED)
            {
                var mousePressedEvent = (MousePressedEvent) event;
                if (Physics2D.pointInsideAABB(new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos), position, buttonSize))
                {
                    currentColor = ON_COLOR;
                    buttonPressAction.run();
                    requiresRedraw = true;
                }
            }
            if (event.eventType == EventType.MOUSE_RELEASED)
            {
                requiresRedraw = currentColor != OFF_COLOR;
                currentColor = OFF_COLOR;
            }
        }
        return new UIEventResult(requiresRedraw, false);
    }

    @Override
    public void draw(Batch2D batch)
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
