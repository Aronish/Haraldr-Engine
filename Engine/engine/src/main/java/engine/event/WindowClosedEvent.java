package engine.event;

public class WindowClosedEvent extends Event
{
    public WindowClosedEvent()
    {
        super(EventType.WINDOW_CLOSED, EventCategory.CATEGORY_WINDOW);
    }
}
