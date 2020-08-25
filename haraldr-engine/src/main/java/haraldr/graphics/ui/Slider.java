package haraldr.graphics.ui;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Renderer2D;
import haraldr.input.Button;
import haraldr.input.Input;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class Slider extends LabeledComponent
{
    private Vector2f sliderPosition = new Vector2f(), sliderSize;
    private Vector2f handlePosition = new Vector2f(), handleSize;

    private float value;
    private boolean held;
    private SliderChangeAction sliderChangeAction;

    public Slider(String name, Pane parent)
    {
        this(name, parent, (value) -> {});
    }

    public Slider(String name, Pane parent, SliderChangeAction sliderChangeAction)
    {
        super(name, parent);
        sliderSize = new Vector2f(parent.size.getX() - parent.getDivider(), label.getFont().getSize());
        handleSize = new Vector2f(20f, label.getFont().getSize());
        this.sliderChangeAction = sliderChangeAction;
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
    public void setPosition(Vector2f position)
    {
        super.setPosition(position);
        sliderPosition = Vector2f.add(position, new Vector2f(parent.getDivider(), 0f));
        handlePosition = new Vector2f(sliderPosition);
    }

    @Override
    public void onEvent(Event event)
    {
        if (event.eventType == EventType.MOUSE_PRESSED)
        {
            if (Input.wasMouseButton(event, Button.MOUSE_BUTTON_1))
            {
                var mousePressedEvent = (MousePressedEvent) event;
                if (mousePressedEvent.xPos > handlePosition.getX() &&
                        mousePressedEvent.xPos < handlePosition.getX() + handleSize.getX() &&
                        mousePressedEvent.yPos > handlePosition.getY() &&
                        mousePressedEvent.yPos < handlePosition.getY() + handleSize.getY())
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
                value = (position - sliderPosition.getX()) / (sliderSize.getX() - handleSize.getX());
                sliderChangeAction.run(value);
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
        Renderer2D.drawQuad(sliderPosition, sliderSize, new Vector4f(0.5f, 0.5f, 0.5f, 1f));
        Renderer2D.drawQuad(handlePosition, handleSize, new Vector4f(1f));
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
