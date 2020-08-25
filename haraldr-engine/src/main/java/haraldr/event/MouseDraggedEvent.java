package haraldr.event;
//TODO: Maybe useless due to "viscous" mouse movement.
public class MouseDraggedEvent extends Event
{
    public final double xPos, yPos;

    public MouseDraggedEvent(double xPos, double yPos)
    {
        super(EventType.MOUSE_DRAGGED, EventCategory.CATEGORY_INPUT, EventCategory.CATEGORY_MOUSE);
        this.xPos = xPos;
        this.yPos = yPos;
    }
}
