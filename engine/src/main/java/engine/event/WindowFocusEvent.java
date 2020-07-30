package engine.event;

public class WindowFocusEvent extends Event
{
    public boolean focused;

    public WindowFocusEvent(boolean focused)
    {
        super(EventType.WINDOW_FOCUS, EventCategory.CATEGORY_WINDOW);
        this.focused = focused;
    }

    @Override
    public String toString()
    {
        return "Focused: " + focused;
    }
}
