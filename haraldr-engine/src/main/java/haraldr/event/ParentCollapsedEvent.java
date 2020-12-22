package haraldr.event;

public class ParentCollapsedEvent extends Event
{
    public final boolean collapsed;

    public ParentCollapsedEvent(boolean collapsed)
    {
        super(EventType.PARENT_COLLAPSED, EventCategory.CATEGORY_APPLICATION);
        this.collapsed = collapsed;
    }
}
