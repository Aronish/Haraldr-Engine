package haraldr.event;

public class WindowResizedEvent extends Event
{
    public final int width, height;

    public WindowResizedEvent(int width, int height)
    {
        super(EventType.WINDOW_RESIZED, EventCategory.CATEGORY_WINDOW);
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString()
    {
        return String.format("%s: Width: %d Height: %d", super.toString(), width, height);
    }
}