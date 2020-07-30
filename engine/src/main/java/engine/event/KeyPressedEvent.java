package engine.event;

public class KeyPressedEvent extends KeyEvent
{
    public KeyPressedEvent(int keyCode)
    {
        super(keyCode, EventType.KEY_PRESSED, EventCategory.CATEGORY_INPUT, EventCategory.CATEGORY_KEYBOARD);
    }
}
