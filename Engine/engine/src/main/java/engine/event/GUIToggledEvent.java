package engine.event;

public class GUIToggledEvent extends Event
{
    public final boolean visible;

    public GUIToggledEvent(boolean visible)
    {
        super(EventType.GUI_TOGGLED, EventCategory.CATEGORY_APPLICATION);
        this.visible = visible;
    }
}
