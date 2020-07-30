package haraldr.event;

public class DebugScreenUpdatedEvent extends Event
{
    public final int fps, ups;

    public DebugScreenUpdatedEvent(int fps, int ups)
    {
        super(EventType.DEBUG_SCREEN_UPDATED, EventCategory.CATEGORY_APPLICATION);
        this.fps = fps;
        this.ups = ups;
    }
}
