package haraldr.dockspace.uicomponents;

import haraldr.dockspace.ControlPanel;
import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Batch2D;
import haraldr.input.MouseButton;
import haraldr.input.Input;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

@SuppressWarnings("unused")
public class Slider extends LabeledComponent
{
    private Vector2f sliderPosition = new Vector2f(), sliderSize;
    private Vector2f handlePosition = new Vector2f(), handleSize;

    private float value, min, max;
    private boolean held;
    private SliderChangeAction sliderChangeAction;

    public Slider(String name, ControlPanel parent)
    {
        this(name, parent, (value) -> {});
    }

    public Slider(String name, ControlPanel parent, SliderChangeAction sliderChangeAction)
    {
        this(name, parent, 0f, 1f, sliderChangeAction);
    }

    public Slider(String name, ControlPanel parent, float min, float max, SliderChangeAction sliderChangeAction)
    {
        super(name, parent);
        sliderSize = new Vector2f(parent.getComponentDivisionSize(), label.getFont().getSize());
        handleSize = new Vector2f(20f, label.getFont().getSize());
        this.sliderChangeAction = sliderChangeAction;
        this.min = min;
        this.max = max;
    }

    public void setValue(float value)
    {
        this.value = value;
        handlePosition.setX(value * (sliderSize.getX() - handleSize.getX()) + sliderPosition.getX());
        sliderChangeAction.run(value);
    }

    public void setSliderChangeAction(SliderChangeAction sliderChangeAction)
    {
        this.sliderChangeAction = sliderChangeAction;
    }

    @Override
    public void setComponentPosition(Vector2f position)
    {
        sliderPosition = position;
        float normalizedValue = (value - min) / (max - min);
        float handlePosition = normalizedValue * (sliderSize.getX() - handleSize.getX()) + sliderPosition.getX();
        this.handlePosition.set(handlePosition, position.getY());
    }

    @Override
    public void setWidth(float width)
    {
        sliderSize.setX(width);
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
                if (Physics2D.pointInsideAABB(new Vector2f(mousePressedEvent.xPos, mousePressedEvent.yPos), handlePosition, handleSize))
                {
                    held = true;
                }
            }
        }
        if (event.eventType == EventType.MOUSE_RELEASED) held = false;
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            var mouseMovedEvent = (MouseMovedEvent) event;
            if (held)
            {
                float position = (float) mouseMovedEvent.xPos - handleSize.getX() / 2f;
                if (position < sliderPosition.getX()) position = sliderPosition.getX();
                if (position > sliderPosition.getX() + sliderSize.getX() - handleSize.getX()) position = sliderPosition.getX() + sliderSize.getX() - handleSize.getX();
                handlePosition.setX(position);
                float normalizedValue = (position - sliderPosition.getX()) / (sliderSize.getX() - handleSize.getX());
                value = min + (max - min) * normalizedValue;
                sliderChangeAction.run(value);
                requireRedraw = true;
            }
        }
        return requireRedraw;
    }

    @Override
    public void render(Batch2D batch)
    {
        batch.drawQuad(sliderPosition, sliderSize, new Vector4f(0.5f, 0.5f, 0.5f, 1f));
        batch.drawQuad(handlePosition, handleSize, new Vector4f(1f));
    }

    @Override
    public float getVerticalSize()
    {
        return sliderSize.getY();
    }

    public float getValue()
    {
        return value;
    }

    @FunctionalInterface
    public interface SliderChangeAction
    {
        void run(float value);
    }
}
