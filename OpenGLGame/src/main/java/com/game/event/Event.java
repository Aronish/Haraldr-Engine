package com.game.event;

public abstract class Event
{
    public final EventType eventType;
    private int eventCategoryFlags;
    private boolean isHandled;

    public Event(EventType eventType, EventCategory... eventCategories)
    {
        this.eventType = eventType;
        for (EventCategory eventCategory : eventCategories)
        {
            eventCategoryFlags |= eventCategory.bitFlag;
        }
    }

    public void setHandled(boolean handled)
    {
        isHandled = handled;
    }

    public boolean isHandled()
    {
        return isHandled;
    }

    public boolean isInCategory(EventCategory eventCategory)
    {
        return (eventCategoryFlags & eventCategory.bitFlag) != 0;
    }

    public boolean isInCategory(int eventCategoryFlags)
    {
        return (this.eventCategoryFlags & eventCategoryFlags) != 0;
    }

    @Override
    public String toString()
    {
        return String.format("(%s)", eventType.toString());
    }
}
