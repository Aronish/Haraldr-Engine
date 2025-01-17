package haraldr.event;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class Event
{
    public final EventType eventType;
    private int eventCategoryFlags;

    @Contract(pure = true)
    public Event(EventType eventType, @NotNull EventCategory... eventCategories)
    {
        this.eventType = eventType;
        for (EventCategory eventCategory : eventCategories)
        {
            eventCategoryFlags |= eventCategory.bitFlag;
        }
    }

    public boolean isInCategory(@NotNull EventCategory eventCategory)
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
