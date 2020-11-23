package haraldr.dockspace.uicomponents;

import haraldr.debug.Logger;
import haraldr.dockspace.ControlPanel;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Batch2D;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

public class Button extends LabeledComponent
{
    private static final Vector4f ON_COLOR = new Vector4f(0.2f, 0.8f, 0.3f, 1f);
    private static final Vector4f OFF_COLOR = new Vector4f(0.8f, 0.2f, 0.3f, 1f);
    private static final Vector4f HOVER_COLOR = new Vector4f(0.2f, 0.7f, 0.4f, 1f);
    private static final Vector4f DISABLED_COLOR = new Vector4f(0.4f, 0.4f, 0.4f, 1f);

    private Vector2f buttonPosition = new Vector2f(), buttonSize;
    private Vector4f currentColor = OFF_COLOR;
    private ButtonPressAction buttonPressAction;

    public Button(String name, ControlPanel parent)
    {
        this(name, parent, () -> {});
    }

    public Button(String name, ControlPanel parent, ButtonPressAction buttonPressAction)
    {
        super(name, parent);
        buttonSize = new Vector2f(parent.getComponentDivisionSize(), label.getFont().getSize());
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
    public void setComponentPosition(Vector2f position)
    {
        buttonPosition = position;
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
        if (enabled)
        {
            if (event.eventType == EventType.MOUSE_PRESSED)
            {
                var mousePressedEvent = (MousePressedEvent) event;
                if (Physics2D.pointInsideAABB(new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos), buttonPosition, buttonSize))
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
            if (event.eventType == EventType.MOUSE_MOVED)
            {
                var mouseMovedEvent = (MouseMovedEvent) event;
                Vector4f lastColor = currentColor;
                currentColor = (Physics2D.pointInsideAABB(new Vector2f(mouseMovedEvent.xPos, mouseMovedEvent.yPos), buttonPosition, buttonSize)) ? HOVER_COLOR : OFF_COLOR;
                requireRedraw = lastColor != currentColor;
            }
        }
        return requireRedraw;
    }

    @Override
    public void render(Batch2D batch)
    {
        batch.drawQuad(buttonPosition, buttonSize, currentColor);
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
