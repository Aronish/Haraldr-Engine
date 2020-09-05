package haraldr.event;

public abstract class MouseButtonEvent extends Event
{
    public final int button;
    public final int xPos, yPos;

    public MouseButtonEvent(int button, int xPos, int yPos, EventType eventType, EventCategory... eventCategories)
    {
        super(eventType, eventCategories);
        this.button = button;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    @Override
    public String toString()
    {
        return String.format("%s: Button: %d XPos: %d YPos: %d", super.toString(), button, xPos, yPos);
    }
}
