package com.game.event;

public class KeyPressedEvent extends KeyEvent {

    public KeyPressedEvent(int keyCode) {
        super(EventType.KEY_PRESSED, keyCode);
    }
}
