package event;

public abstract class MouseButtonEvent extends Event
{
    public final int button;

    public MouseButtonEvent(int button, EventType eventType, EventCategory... eventCategories)
    {
        super(eventType, eventCategories);
        this.button = button;
    }

    @Override
    public String toString()
    {
        return String.format("%s: Button: %d", super.toString(), button);
    }
}
