package engine.event;

public class MouseScrolledEvent extends Event
{
    public final double xOffset, yOffset;

    public MouseScrolledEvent(double xOffset, double yOffset)
    {
        super(EventType.MOUSE_SCROLLED, EventCategory.CATEGORY_INPUT, EventCategory.CATEGORY_MOUSE);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public String toString()
    {
        return String.format("%s: XOffset: %f YOffset: %f", super.toString(), xOffset, yOffset);
    }
}
