package com.game.event;

public class KeyReleasedEvent extends KeyEvent {

    public KeyReleasedEvent(int keyCode) {
        super(EventType.KEY_RELEASED, keyCode);
    }
}
