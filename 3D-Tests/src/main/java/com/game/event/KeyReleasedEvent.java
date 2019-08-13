package com.game.event;

public class KeyReleasedEvent extends KeyEvent {

    public KeyReleasedEvent(int keyCode) {
        super(keyCode, EventType.KEY_RELEASED, EventCategory.CATEGORY_INPUT, EventCategory.CATEGORY_KEYBOARD);
    }
}
