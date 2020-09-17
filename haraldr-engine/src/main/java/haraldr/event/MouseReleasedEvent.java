package haraldr.event;

public class MouseReleasedEvent extends MouseButtonEvent
{
    public MouseReleasedEvent(int button, int xPos, int yPos)
    {
        super(button, xPos, yPos, EventType.MOUSE_RELEASED, EventCategory.CATEGORY_INPUT, EventCategory.CATEGORY_MOUSE, EventCategory.CATEGORY_MOUSE_BUTTON);
    }
}
