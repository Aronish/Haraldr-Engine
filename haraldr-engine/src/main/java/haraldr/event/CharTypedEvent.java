package haraldr.event;

public class CharTypedEvent extends Event
{
    public final char character;

    public CharTypedEvent(int codePoint)
    {
        super(EventType.CHAR_TYPED, EventCategory.CATEGORY_INPUT, EventCategory.CATEGORY_KEYBOARD);
        character = (char) codePoint;
    }

    @Override
    public String toString()
    {
        return String.format("%s: Char typed: %c", super.toString(), character);
    }
}
