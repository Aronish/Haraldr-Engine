package com.game.event;

public abstract class KeyEvent extends Event {

    public final int keyCode;

    public KeyEvent(EventType eventType, int keyCode){
        super(eventType);
        this.keyCode = keyCode;
    }
}
