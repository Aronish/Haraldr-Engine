package haraldr.ui;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MousePressedEvent;
import haraldr.graphics.Batch2D;
import haraldr.input.Input;
import haraldr.input.MouseButton;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;
import haraldr.physics.Physics2D;

public class UISlider extends UIComponent
{
    private Vector2f sliderSize;
    private Vector2f handlePosition = new Vector2f(), handleSize;

    private float value, min, max;
    private boolean held;
    private SliderChangeAction sliderChangeAction;

    public UISlider()
    {
        this(null, 0f, 1f, 0f, value -> {});
    }

    public UISlider(UIContainer parent)
    {
        this(parent, 0f, 1f, 0f, value -> {});
    }

    public UISlider(UIContainer parent, SliderChangeAction sliderChangeAction)
    {
        this(parent, 0f, 1f, 0f, sliderChangeAction);
    }

    public UISlider(UIContainer parent, float defaultValue, SliderChangeAction sliderChangeAction)
    {
        this(parent, 0f, 1f, defaultValue, sliderChangeAction);
    }

    public UISlider(UIContainer parent, float min, float max, SliderChangeAction sliderChangeAction)
    {
        this(parent, min, max, 0f, sliderChangeAction);
    }

    public UISlider(UIContainer parent, float min, float max, float defaultValue, SliderChangeAction sliderChangeAction)
    {
        super(parent);
        sliderSize = new Vector2f(20f);
        handleSize = new Vector2f(20f);
        this.sliderChangeAction = sliderChangeAction;
        this.min = min;
        this.max = max;
        setValue(defaultValue);
    }

    public void setValue(float value)
    {
        this.value = value;
        handlePosition.setX(value * (sliderSize.getX() - handleSize.getX()) + position.getX());
        sliderChangeAction.run(value);
    }

    public void setSliderChangeAction(SliderChangeAction sliderChangeAction)
    {
        this.sliderChangeAction = sliderChangeAction;
    }

    private void recalculateHandlePosition()
    {
        float normalizedValue = (value - min) / (max - min);
        float handlePosition = normalizedValue * (sliderSize.getX() - handleSize.getX()) + position.getX();
        this.handlePosition.set(handlePosition, position.getY());
    }

    @Override
    public void setPosition(Vector2f position)
    {
        this.position.set(position);
        recalculateHandlePosition();
    }

    @Override
    public void setWidth(float width)
    {
        sliderSize.setX(width);
        recalculateHandlePosition();
    }

    @Override
    public boolean onEvent(Event event, Window window)
    {
        boolean requiresRedraw = false;
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
                if (position < this.position.getX()) position = this.position.getX();
                if (position > this.position.getX() + sliderSize.getX() - handleSize.getX()) position = this.position.getX() + sliderSize.getX() - handleSize.getX();
                handlePosition.setX(position);
                float normalizedValue = (position - this.position.getX()) / (sliderSize.getX() - handleSize.getX());
                value = min + (max - min) * normalizedValue;
                sliderChangeAction.run(value);
                requiresRedraw = true;
            }
        }
        return requiresRedraw;
    }

    @Override
    public void draw(Batch2D batch)
    {
        batch.drawQuad(position, sliderSize, new Vector4f(0.5f, 0.5f, 0.5f, 1f));
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
