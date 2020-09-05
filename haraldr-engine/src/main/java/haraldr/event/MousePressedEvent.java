package haraldr.event;

public class MousePressedEvent extends MouseButtonEvent
{
    public MousePressedEvent(int button, int xPos, int yPos)
    {
        super(button, xPos, yPos, EventType.MOUSE_PRESSED, EventCategory.CATEGORY_INPUT, EventCategory.CATEGORY_MOUSE);
    }
}
