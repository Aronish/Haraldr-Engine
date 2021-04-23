package haraldr.event;

public class FrameInfoUpdatedEvent extends Event
{
    public final int fps, ups;
    public final double frameTime;

    public FrameInfoUpdatedEvent(int fps, int ups, double frameTime)
    {
        super(EventType.FRAME_INFO_UPDATED, EventCategory.CATEGORY_APPLICATION);
        this.fps = fps;
        this.ups = ups;
        this.frameTime = frameTime;
    }
}
